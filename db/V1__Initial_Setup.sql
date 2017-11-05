CREATE TABLE dbo.query
(
	id bigint PRIMARY KEY NOT NULL IDENTITY(1,1),
	query ntext NOT NULL
)
GO

CREATE TABLE dbo.result
(
	id bigint PRIMARY KEY NOT NULL IDENTITY(1,1),
	query_id bigint NOT NULL,
	hyperlink ntext NOT NULL,
	constraint FK_query_result foreign key (query_id) references dbo.query (id)
)
GO