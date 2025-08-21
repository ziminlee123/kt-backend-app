Get-Content sql\data.sql -Encoding UTF8 | docker exec -i postgres_db psql -U kt -d mydb

