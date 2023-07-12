import pymysql
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.chrome.options import Options
import time
import pandas as pd
import json
import sys
import codecs

def crawl_with_title(title):
    # 크롤링 부분
    chrome_options = Options()
    chrome_options.add_argument("--headless")

    chromedriver_path = 'D:/gpt/chromedriver'

    driver = webdriver.Chrome(options=chrome_options)
    url = 'https://shopping.naver.com/home'
    driver.get(url)
    search = driver.find_element(By.CSS_SELECTOR, '#__next > div > div.pcHeader_header__eETRe > div > div > div._gnb_header_area_150KE > div > div._gnbLogo_gnb_logo_3eIAf > div > div._gnbSearch_gnb_search_3O1L2 > form > div._gnbSearch_inner_2Zksb > div > input')

    search.send_keys(title)

    search.send_keys(Keys.ENTER)

    for _ in range(2):
        driver.find_element(By.TAG_NAME, "body").send_keys(Keys.END)
        time.sleep(1)

    # 상품명 요소들 찾기
    name_elements = driver.find_elements(By.CSS_SELECTOR, 'div.product_title__Mmw2K a.product_link__TrAac')

    # 가격 요소들 찾기
    price_elements = driver.find_elements(By.CSS_SELECTOR, 'span.price_price__LEGN7 span.price_num__S2p_v')

    data = []

    # 요소들을 순회하며 상품명과 가격 추출하여 데이터 리스트에 저장
    for i in range(5):
        name = name_elements[i].text
        if i + 3 < len(price_elements):
            price = price_elements[i+3].text
            data.append([name, price])

    # 데이터를 JSON 형식으로 출력
    json_data = json.dumps(data, ensure_ascii=False)
    sys.stdout.buffer.write(codecs.encode(json_data, 'utf-8'))
    sys.stdout.flush()

    sys.exit(0)

if __name__ == '__main__':
    input_value = sys.argv[1]
    crawl_with_title(input_value)