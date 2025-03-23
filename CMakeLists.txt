cmake_minimum_required(VERSION 3.28)

Project(FlockHistory)

################################################################################

add_library(sqlite3
	libs/sqlite-amalgamation-3490000/sqlite3.c
)

target_include_directories(sqlite3
	PUBLIC libs/sqlite-amalgamation-3490000/
)


add_library(sqlite3xx INTERFACE)

target_include_directories(sqlite3xx
	INTERFACE libs/sqliteXX3/src
)

target_link_libraries(sqlite3xx
	INTERFACE sqlite3
)

################################################################################

add_subdirectory(libs/leptonica)
include_directories(
	PUBLIC libs/leptonica/src/
	PUBLIC ${CMAKE_CURRENT_BINARY_DIR}/libs/tesseract/include/)

add_subdirectory(libs/tesseract)

add_subdirectory(libs/Catch2)

################################################################################

add_executable(unit-test EXCLUDE_FROM_ALL
	test/db_interface.cc
)

target_link_libraries(unit-test
	asan
	ubsan
	Catch2::Catch2WithMain
	sqlite3xx
)

target_compile_options(unit-test
	PRIVATE -fsanitize=address
	PRIVATE -fsanitize=undefined
)

################################################################################

add_executable(tag_ocr_test EXCLUDE_FROM_ALL
	test/tag_ocr_test.cc
)


target_link_libraries(tag_ocr_test
	leptonica
	# libleptonica.a
	libtesseract
)

target_link_options(tag_ocr_test
	PRIVATE -L${CMAKE_BINARY_DIR}/libs/leptonica/src
	PRIVATE -lleptonica)
