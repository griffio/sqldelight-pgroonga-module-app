package griffio

import app.cash.sqldelight.dialect.api.IntermediateType
import app.cash.sqldelight.dialect.api.PrimitiveType
import app.cash.sqldelight.dialect.api.SqlDelightModule
import app.cash.sqldelight.dialect.api.TypeResolver
import app.cash.sqldelight.dialects.postgresql.PostgreSqlTypeResolver
import app.cash.sqldelight.dialects.postgresql.grammar.PostgreSqlParser
import app.cash.sqldelight.dialects.postgresql.grammar.PostgreSqlParserUtil
import com.alecstrong.sql.psi.core.psi.SqlExpr
import com.alecstrong.sql.psi.core.psi.SqlFunctionExpr
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.lang.parser.GeneratedParserUtilBase.Parser
import griffio.grammar.PgroongaParser
import griffio.grammar.PgroongaParserUtil
import griffio.grammar.PgroongaParserUtil.extension_expr
import griffio.grammar.PgroongaParserUtil.index_method
import griffio.grammar.PgroongaParserUtil.storage_parameters
import griffio.grammar.PgroongaParserUtil.storage_parameter

import griffio.grammar.psi.PGroonaExtensionExpr

class PGroongaModule : SqlDelightModule {
    override fun typeResolver(parentResolver: TypeResolver): TypeResolver = PGroongaTypeResolver(parentResolver)

    override fun setup() {

        val previousExtensionExpr = PostgreSqlParserUtil.extension_expr
        val previousIndexMethod = PostgreSqlParserUtil.index_method
        val previousStorageParameters = PostgreSqlParserUtil.storage_parameters
        val previousStorageParameter = PostgreSqlParserUtil.storage_parameter

        PgroongaParserUtil.reset()
        PgroongaParserUtil.overridePostgreSqlParser()
        // As the grammar doesn't support inheritance - override type_name manually to try inherited type_name

        PostgreSqlParserUtil.extension_expr = Parser { psiBuilder, i ->
            extension_expr?.parse(psiBuilder, i)
                    ?: PgroongaParser.extension_expr_real(psiBuilder, i)
                    || previousExtensionExpr?.parse(psiBuilder, i)
                    ?: PostgreSqlParser.extension_expr_real(psiBuilder, i)
        }

        // etc
        PostgreSqlParserUtil.index_method = GeneratedParserUtilBase.Parser { psiBuilder, i ->
            index_method?.parse(psiBuilder, i)
                    ?: PgroongaParser.index_method_real(psiBuilder, i)
                    || previousIndexMethod?.parse(psiBuilder, i)
                    ?: PostgreSqlParser.index_method_real(psiBuilder, i)
        }

        PostgreSqlParserUtil.storage_parameters = GeneratedParserUtilBase.Parser { psiBuilder, i ->
            storage_parameters?.parse(psiBuilder, i)
                    ?: PgroongaParser.storage_parameters_real(psiBuilder, i)
                    || previousStorageParameters?.parse(psiBuilder, i)
                    ?: PostgreSqlParser.storage_parameters_real(psiBuilder, i)
        }

        PostgreSqlParserUtil.index_method = GeneratedParserUtilBase.Parser { psiBuilder, i ->
            storage_parameter?.parse(psiBuilder, i)
                    ?: PgroongaParser.storage_parameter_real(psiBuilder, i)
                    || previousStorageParameter?.parse(psiBuilder, i)
                    ?: PostgreSqlParser.storage_parameter_real(psiBuilder, i)
        }
    }
}

//// Change to inheritance so that definitionType can be called by polymorphism - not possible with delegation
class PGroongaTypeResolver(private val parentResolver: TypeResolver) : PostgreSqlTypeResolver(parentResolver) {

    override fun resolvedType(expr: SqlExpr) : IntermediateType {
        return if (expr is PGroonaExtensionExpr && expr.pgroonaOperatorExpression != null)
            IntermediateType(PrimitiveType.BOOLEAN) else super.resolvedType(expr)
    }

    override fun functionType(functionExpr: SqlFunctionExpr): IntermediateType? =
        when (functionExpr.functionName.text.lowercase()) {
            "pgroonga_score" -> IntermediateType(PrimitiveType.REAL)
            "pgroonga_highlight_html" -> IntermediateType(PrimitiveType.TEXT)
            "pgroonga_query_extract_keywords" -> arrayIntermediateType(IntermediateType(PrimitiveType.TEXT))
            else -> super.functionType(functionExpr)
        }
}
