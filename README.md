# SqlDelight 2.1.x Postgresql module support prototype 

https://github.com/cashapp/sqldelight

**Experimental**

Pgroonga https://pgroonga.github.io/

Use with SqlDelight `2.1.0`

TODO

* Add functions `pgroonga_highlight_html` `pgroonga_query_extract_keywords` `pgroonga_score`
* Needs to support `pgroonga_score` with system columns `tableoid, ctid` 

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
