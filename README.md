# SqlDelight 2.1.x Postgresql module support prototype 

https://github.com/cashapp/sqldelight

**Experimental**

Pgroonga https://pgroonga.github.io/

Use with SqlDelight `2.1.0`

```sql

CREATE EXTENSION IF NOT EXISTS pgroonga;

CREATE TABLE Memos (
  id INTEGER,
  content TEXT
);

CREATE INDEX pgroonga_content_index ON Memos USING pgroonga (content)
        WITH (tokenizer='TokenNgram("report_source_location", true)', normalizer='NormalizerNFKC100');

CREATE TABLE ScoreMemos (
  id integer PRIMARY KEY,
  content text
);

CREATE INDEX pgroonga_score_memos_content_index ON ScoreMemos USING pgroonga (content);

searchScoreMemos:
SELECT *, pgroonga_score(tableoid, ctid) AS score
FROM ScoreMemos
WHERE content &@ 'PGroonga' OR content &@ 'PostgreSQL';

searchHighlightHtml:
SELECT pgroonga_highlight_html(content, :keywords::TEXT[]) AS highlight_html
FROM Memos;
```

TODO

* Add functions `pgroonga_highlight_html` `pgroonga_query_extract_keywords` `pgroonga_score`
* Needs to support `pgroonga_score` with system columns `tableoid, ctid`
  https://github.com/sqldelight/sqldelight/pull/5834

---

```shell
docker run \
  --name pgroonga-demo \
  -e POSTGRES_PASSWORD=PGroonga \
  -e POSTGRES_DB=PGroonga \
  -e POSTGRES_USER=PGroonga \
  -p 5432:5432 \
  -d groonga/pgroonga:4.0.1-alpine-16-slim
```
