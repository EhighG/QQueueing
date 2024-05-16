'''
bash 실행

1. 가상환경 설정
python -m venv venv

2. 가상환경 활성화
source venv/Scripts/activate

3. requirements.txt 작성된 라이브러리 설치
pip install -r requirements.txt

4. selenium_test.py 실행
python selenium_test.py
'''

# selenium의 webdriver를 사용하기 위한 import
from selenium import webdriver

# selenium으로 키를 조작하기 위한 import
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By

# 페이지 로딩을 기다리는데에 사용할 time 모듈 import
import time
from threading import Thread

from selenium.webdriver.chrome.options import Options

options = Options()
options.add_argument("--headless")  # Headless 모드 활성화

def simulate_user():
    # 크롬드라이버 실행
    # driver = webdriver.Chrome(options=options)  # 백그라운드 실행
    driver = webdriver.Chrome()  # 창 실행

    #크롬 드라이버에 url 주소 넣고 실행
    driver.get('https://k10a401.p.ssafy.io/')

    # 페이지가 완전히 로딩되도록 기다림
    time.sleep(10)

    search_button = driver.find_element(By.XPATH, '/html/body/main/div[2]/div/div[2]/div/div[3]/button')
    # search_button = driver.find_element(By.XPATH, '/html/body/main/div[2]/div/div[8]/div/div[3]/button')

    search_button.click()

    time.sleep(20)

    driver.quit()

# 쓰레드 수 조절
threads = []
for _ in range(10):
    t = Thread(target=simulate_user)
    t.start()
    threads.append(t)
    time.sleep(0.03)

for t in threads:
    t.join()