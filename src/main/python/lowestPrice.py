import pymysql
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.options import Options
import time
import openai
import pandas as pd

# The part that connects to Mysqldb and gets the title

db_host = 'localhost'
db_user = 'root'
db_password = '1234'
db_name = 'minty'

db_connection = pymysql.connect(
    host=db_host,
    user=db_user,
    password=db_password,
    database=db_name,
    charset='utf8'
)
db_cursor = db_connection.cursor()

query = "SELECT name, gpt_id FROM gpt"

try:
    db_cursor.execute(query)

    # Fetch the first row returned by the query
    rows = db_cursor.fetchall()

    # Iterate over each row
    for row in rows:
        title = row[0]
        id = row[1]

        # gptApi가 상품이름 분석하는 부분

        openai.api_key = "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"

        messages = []

        fixed_question = "이어지는 문장에서 상품명만 알려줘. "

        answer = ""

        while True:
            messages.append({"role": "user", "content": fixed_question})
            messages.append({"role": "user", "content": title})

            completion = openai.ChatCompletion.create(model="gpt-3.5-turbo", messages=messages)
            assistant_content = completion.choices[0].message["content"].strip()

            messages.append({"role": "assistant", "content": assistant_content})

            if len(messages) > 1:
                answer = assistant_content
                break

        # 크롤링 부분

        chrome_options = Options()
        chrome_options.add_argument("--headless")

        chromedriver_path = 'D:/gpt/chromedriver'

        driver = webdriver.Chrome(options=chrome_options)
        url = 'https://shopping.naver.com/home'
        driver.get(url)

        search = driver.find_element(By.CSS_SELECTOR, '#__next > div > div.pcHeader_header__tXOY4 > div > div > div._gnb_header_area_150KE > div > div._gnbLogo_gnb_logo_3eIAf > div > div._gnbSearch_gnb_search_3O1L2 > form > div._gnbSearch_inner_2Zksb > div > input')

        search.send_keys(answer)

        search.send_keys(Keys.ENTER)

        for _ in range(2):
            driver.find_element(By.TAG_NAME, "body").send_keys(Keys.END)
            time.sleep(1)

        # 상품명 요소들 찾기
        name_elements = driver.find_elements(By.CSS_SELECTOR, 'div.product_title__Mmw2K a.product_link__TrAac')

        # 가격 요소들 찾기
        price_elements = driver.find_elements(By.CSS_SELECTOR, 'span.price_price__LEGN7 span.price_num__S2p_v')

        data = []
        data_id = 0

        # 요소들을 순회하며 상품명과 가격 추출하여 데이터 리스트에 저장
        for i, (name_element, price_element) in enumerate(zip(name_elements, price_elements)):
            name = name_element.text
            price = price_element.text
            data.append([name, price])

            # 3개 아이템만 저장 후 종료
            if i + 1 == 10:
                break

        # Establish a connection to the MySQL database
        db_connection = pymysql.connect(
            host=db_host,
            user=db_user,
            password=db_password,
            database=db_name,
            charset='utf8'
        )
        db_cursor = db_connection.cursor()

        get_max_data_id_query = "SELECT MAX(data_id) FROM minty.data"
        db_cursor.execute(get_max_data_id_query)
        max_data_id = db_cursor.fetchone()[0]

        if max_data_id:
            data_id = max_data_id

        for item in data:
            name = item[0]
            price = item[1]
            data_id += 1

            # SQL query to insert the data into the table
            insert_query = f"INSERT INTO minty.data (data_id, name, price,gpt_id) VALUES ('{data_id}','{name}', '{price}','{id}')"

            try:
                db_cursor.execute(insert_query)
                db_connection.commit()
                print("Data inserted successfully.")
            except pymysql.Error as e:
                db_connection.rollback()
                print(f"Error inserting data: {e}")

        # Fetch the next row
        row = db_cursor.fetchone()

except pymysql.Error as e:
    print(f"Error retrieving data from database: {e}")

# Close the database connection
db_cursor.close()
db_connection.close()