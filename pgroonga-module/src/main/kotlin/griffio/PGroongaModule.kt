package griffio

import app.cash.sqldelight.dialect.api.DialectType
import app.cash.sqldelight.dialect.api.IntermediateType
import app.cash.sqldelight.dialect.api.PrimitiveType
import app.cash.sqldelight.dialect.api.SqlDelightModule
import app.cash.sqldelight.dialect.api.TypeResolver
import app.cash.sqldelight.dialects.postgresql.PostgreSqlType
import app.cash.sqldelight.dialects.postgresql.PostgreSqlTypeResolver
import app.cash.sqldelight.dialects.postgresql.grammar.PostgreSqlParser
import app.cash.sqldelight.dialects.postgresql.grammar.PostgreSqlParserUtil
import com.alecstrong.sql.psi.core.psi.SqlExpr
import com.alecstrong.sql.psi.core.psi.SqlFunctionExpr
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.lang.parser.GeneratedParserUtilBase.Parser
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.asTypeName
import griffio.grammar.PgroongaParser
import griffio.grammar.PgroongaParserUtil
import griffio.grammar.psi.PGroonaExtensionExpr

class PGroongaModule : SqlDelightModule {
    override fun typeResolver(parentResolver: TypeResolver): TypeResolver = PGroongaTypeResolver(parentResolver)

    override fun setup() {
        PgroongaParserUtil.reset()
        PgroongaParserUtil.overridePostgreSqlParser()
        // As the grammar doesn't support inheritance - override type_name manually to try inherited type_name
        PostgreSqlParserUtil.extension_expr = GeneratedParserUtilBase.Parser { psiBuilder, i ->
            PgroongaParserUtil.extension_expr?.parse(psiBuilder, i) ?: PgroongaParser.extension_expr_real(psiBuilder, i)
                    || PostgreSqlParser.extension_expr_real(psiBuilder, i)
        }

        // etc
        PostgreSqlParserUtil.index_method = GeneratedParserUtilBase.Parser { psiBuilder, i ->
            PgroongaParserUtil.index_method?.parse(psiBuilder, i) ?: PgroongaParser.index_method_real(psiBuilder, i)
                    || PostgreSqlParser.index_method_real(psiBuilder, i)
        }

        PostgreSqlParserUtil.storage_parameters = Parser { psiBuilder, i ->
            PgroongaParserUtil.storage_parameters?.parse(psiBuilder, i) ?: PgroongaParser.storage_parameters_real(psiBuilder, i)
                    || PostgreSqlParser.storage_parameters_real(psiBuilder, i)
        }

        PostgreSqlParserUtil.storage_parameter = Parser { psiBuilder, i ->
            PgroongaParserUtil.storage_parameter?.parse(psiBuilder, i) ?: PgroongaParser.storage_parameter_real(psiBuilder, i)
                    || PostgreSqlParser.storage_parameter_real(psiBuilder, i)
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
