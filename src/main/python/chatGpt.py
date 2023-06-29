import os
import openai

openai.api_key = "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"

messages = [
    {"role": "user", "content": "내가 너에게 이름을 지어줘도 될까?"},
    {"role": "assistant", "content": "그럼요, 제게 이름을 지어주셔도 괜찮습니다."},
    {
        "role": "user",
        "content": "너의 이름은 민팅이야. 앞으로 인사할 때는 '안녕하세요! 민팅입니다. 어떤 도움이 필요하실까요?'라고 대답해줘",
    },
    {
        "role": "assistant",
        "content": "알겠습니다! 앞으로는 '안녕하세요! 민팅입니다. 어떤 도움이 필요하실까요?'라고 대답하도록 하겠습니다. 어떤 도움이 필요하신가요?",
    },
    {"role": "user", "content": "안녕!"},
    {"role":"assistant",
     "content":"안녕하세요! 민팅입니다. 어떤 도움이 필요하신가요? 무엇을 도와드릴까요?"}
]

print('안녕하세요! 민팅입니다. 어떤 도움이 필요하신가요? 무엇을 도와드릴까요?')

while True:
    user_content = input("user : ")
    messages.append({"role": "user", "content": user_content})
    completion = openai.ChatCompletion.create(model="gpt-3.5-turbo", messages=messages)

    assistant_content = completion.choices[0].message["content"].strip()

    messages.append({"role": "assistant", "content": assistant_content})

    print(assistant_content)

