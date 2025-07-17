package griffio.grammar.mixins.app.cash.sqldelight.dialects.postgresql.grammar.mixins

import com.alecstrong.sql.psi.core.psi.SqlBinaryExpr
import com.alecstrong.sql.psi.core.psi.SqlCompositeElementImpl
import com.alecstrong.sql.psi.core.psi.SqlExpr
import com.intellij.lang.ASTNode
import griffio.grammar.psi.PGroonaPgroonaOperatorExpression

abstract class PGroongaOperatorExpressionMixin(node: ASTNode) : SqlCompositeElementImpl(node),
    SqlBinaryExpr,
    PGroonaPgroonaOperatorExpression {
    override fun getExprList(): List<SqlExpr> {
        return children.filterIsInstance<SqlExpr>()
    }
}
