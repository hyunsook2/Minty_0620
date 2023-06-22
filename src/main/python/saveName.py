import tkinter as tk
import pymysql

# MySQL connection settings
db_host = 'localhost'
db_user = 'root'
db_password = '1234'
db_name = 'minty'

def insert_data():
    # MySQL connection
    db_connection = pymysql.connect(
        host=db_host,
        user=db_user,
        password=db_password,
        database=db_name,
        charset='utf8'
    )
    db_cursor = db_connection.cursor()

    name = name_entry.get()

    # Retrieve the current maximum gpt_id
    get_max_gpt_id_query = "SELECT MAX(gpt_id) FROM minty.gpt"
    db_cursor.execute(get_max_gpt_id_query)
    max_gpt_id = db_cursor.fetchone()[0]

    if max_gpt_id:
        gpt_id = max_gpt_id + 1
    else:
        gpt_id = 1

    insert_query = f"INSERT INTO minty.gpt (gpt_id, name) VALUES ('{gpt_id}', '{name}')"

    try:
        db_cursor.execute(insert_query)
        db_connection.commit()
        result_label.configure(text="이름이 성공적으로 저장되었습니다!")
    except pymysql.Error as e:
        db_connection.rollback()
        result_label.configure(text=f"이름 저장에 실패하였습니다.: {e}")

    db_cursor.close()
    db_connection.close()

# Create a tkinter window
window = tk.Tk()
window.title("Data Insertion")
window.geometry("300x150")

# Set the background color to mint
window.configure(background="#b2d8d8")

# Name input label and entry widget
name_label = tk.Label(window, text="Name:", bg="#b2d8d8")
name_label.pack()
name_entry = tk.Entry(window)
name_entry.pack()

# Data insertion button
insert_button = tk.Button(window, text="Insert", command=insert_data, bg="#6abf9e", fg="white")
insert_button.pack()

# Result text label
result_label = tk.Label(window, text="", bg="#b2d8d8")
result_label.pack()

# Run the window
window.mainloop()
