package griffio.grammar.mixins.app.cash.sqldelight.dialects.postgresql.grammar.mixins

import com.alecstrong.sql.psi.core.psi.SqlCompositeElementImpl
import com.alecstrong.sql.psi.core.psi.SqlExpr
import com.intellij.lang.ASTNode
import griffio.grammar.psi.PGroonaPgroonaOperator

abstract class PGroongaOperatorMixin(node: ASTNode) :
    SqlCompositeElementImpl(node),
    SqlExpr, PGroonaPgroonaOperator
