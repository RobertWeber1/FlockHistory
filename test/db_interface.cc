#include <catch2/catch_test_macros.hpp>
#include <sql/database.hpp>


struct MyInt
{
	int value;
};

int to_base_type(MyInt value)
{
	return value.value;
}


TEST_CASE("")
{

	std::remove("./test.db");
	sql::DataBase db("./test.db");

	db.execute(
		"CREATE TABLE IF NOT EXISTS foo "
		"(column_1 INTEGER PRIMARY KEY, "
		" column_2 TEXT NOT NULL, "
		" column_3 REAL DEFAULT 0);");

	db.prepare_statement("INSERT INTO foo VALUES(1, 'test1', 1.234);").run();

	db.prepare_statement("INSERT INTO foo VALUES(?, ?, ?);").bind(2, std::string("test2"), 2.345).run();

	db.prepare_statement("INSERT INTO foo VALUES(?, ?, ?);").bind(MyInt{3}, "test3", 3.456).run();

	auto insert = db.insert_into("INSERT INTO foo VALUES(?, ?, ?);");
	insert
		.values(4, "test4", 4.567)
		.values(5, "test5", 5.678);

	REQUIRE(true);
}
