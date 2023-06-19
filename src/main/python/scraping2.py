import time
from bs4 import BeautifulSoup
from selenium import webdriver
import pymysql
from selenium.webdriver.common.by import By
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC


# MySQL database connection settings
db_host = 'localhost'
db_user = 'root'
db_password = '1234'
db_name = 'minty'

# Establish MySQL connection
db_connection = pymysql.connect(
    host=db_host,
    user=db_user,
    password=db_password,
    database=db_name,
    charset='utf8'
)
db_cursor = db_connection.cursor()

# Configure Selenium webdriver
driver = webdriver.Chrome(service=Service('/path/to/chromedriver'))  # Replace with the path to your ChromeDriver executable

# Get HTML from the website using Selenium
url = 'https://web.joongna.com/search'
driver.get(url)
time.sleep(5)  # Wait for the page to fully load
html = driver.page_source

# Parse HTML using BeautifulSoup
soup = BeautifulSoup(html, 'html.parser')

# Find the container for category information
category_container = soup.find('div', class_='css-1wada3t')

# Check if the category container exists
if category_container is not None:
    # Find top categories using Selenium instead of BeautifulSoup
    top_categories = driver.find_elements(By.CSS_SELECTOR, 'div.css-jex737.e1tqob31')

# Iterate over top categories
for i in range(len(top_categories)):
    top_category = driver.find_elements(By.CSS_SELECTOR, 'div.css-jex737.e1tqob31')[i]
    top_category_name = top_category.text.strip()
    print(top_category_name)
    
    # Find sub categories within the current top category using Selenium
    WebDriverWait(driver, 10).until(EC.element_to_be_clickable((By.CSS_SELECTOR, 'div.css-jex737.e1tqob31')))

    top_category.click()  # Click on the top category to reveal the subcategories
    time.sleep(1)  # Wait for the subcategories to load
    sub_categories = driver.find_elements(By.CSS_SELECTOR, 'div.css-68e5x1.e1tqob31')

    # Create a list for sub categories
    sub_category_list = []
    for sub_category in sub_categories:
        sub_category_name = sub_category.text.strip()
        print(sub_category_name)
        sub_category_list.append(sub_category_name)
    categories={}
    # Store top category and sub categories in the dictionary
    categories[top_category_name] = sub_category_list

    # Navigate back to the category list
    driver.get(url)
    time.sleep(5)  # Wait for the page to fully load

    # Store extracted category information in MySQL
    for top_category, sub_categories in categories.items():
        # Insert top category
        insert_top_category_query = "INSERT INTO top_categories (name) VALUES (%s)"
        top_category_values = (top_category,)
        db_cursor.execute(insert_top_category_query, top_category_values)
        db_connection.commit()

        # Get the ID of the inserted top category
        top_category_id = db_cursor.lastrowid

        # Iterate over sub categories and insert them
        for sub_category in sub_categories:
            insert_sub_category_query = "INSERT INTO sub_categories (name, top_category_id) VALUES (%s, %s)"
            sub_category_values = (sub_category, top_category_id)
            db_cursor.execute(insert_sub_category_query, sub_category_values)
            db_connection.commit()

# Close Selenium webdriver
driver.quit()

# Close MySQL connection
db_cursor.close()
db_connection.close()
