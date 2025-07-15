package griffio

import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import griffio.migrations.Memos
import griffio.migrations.SampleTexts
import griffio.queries.Sample
import org.postgresql.ds.PGSimpleDataSource
import kotlin.random.Random

private fun getSqlDriver() = PGSimpleDataSource().apply {
    setURL("jdbc:postgresql://localhost:5432/PGroonga")
    applicationName = "App Main"
    user = "PGroonga"
    password = "PGroonga"
}.asJdbcDriver()

fun main() {
    val driver = getSqlDriver()
    val sample = Sample(driver)
    sample.pgroongaQueries.insertMemo(Memos(Random.nextInt(), "PostgreSQL is a relational database management system."))
    sample.pgroongaQueries.insertMemo(Memos(Random.nextInt(), "Groonga is a fast full text search engine that supports all languages."))
    sample.pgroongaQueries.insertMemo(Memos(Random.nextInt(), "PGroonga is a PostgreSQL extension that uses Groonga as index."))
    sample.pgroongaQueries.insertMemo(Memos(Random.nextInt(), "PostgreSQL is a relational database management system."))

    sample.pgroongaQueries.searchMemos("engine").executeAsList().forEach(::println)
    println("----pgroonga_highlight_html----")
    sample.pgroongaQueries.searchHighlightHtml(arrayOf("Groonga", "PostgreSQL")).executeAsList().forEach(::println)
    println("----pgroonga_query_extract_keywords----")
    sample.pgroongaQueries.selectExtractKeywords("Groonga (MySQL OR PostgreSQL)").executeAsList().forEach(::println)

}
