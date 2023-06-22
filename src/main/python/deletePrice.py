import pymysql

# Database connection details
db_host = 'localhost'
db_user = 'root'
db_password = '1234'
db_name = 'minty'

try:
    # Establish a connection to the MySQL database
    db_connection = pymysql.connect(
        host=db_host,
        user=db_user,
        password=db_password,
        database=db_name,
        charset='utf8'
    )
    db_cursor = db_connection.cursor()

    # SQL query to delete all rows from the data table
    delete_query = "DELETE FROM data"

    # Execute the delete query
    db_cursor.execute(delete_query)
    db_connection.commit()

    print("All data deleted successfully from the 'data' table.")

except pymysql.Error as e:
    print(f"Error deleting data: {e}")

finally:
    # Close the database connection
    db_cursor.close()
    db_connection.close()
