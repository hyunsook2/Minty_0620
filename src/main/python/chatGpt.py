import tkinter as tk
from tkinter import scrolledtext, font
import openai
import sys

root = tk.Tk()

Nickname = sys.argv[1]
apiKey = sys.argv[2]

openai.api_key = apiKey

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
]

data = []

def send_message():
    user_content = input_box.get("1.0", tk.END).strip()
    messages.append({"role": "user", "content": user_content})

    loading_label.config(text="민팅이가 열심히 생각하고 있으니 기다려주세요!")
    loading_label.lift()
    loading_label.place(relx=0.5, rely=0.5, anchor=tk.CENTER)

    root.update()

    completion = openai.ChatCompletion.create(model="gpt-3.5-turbo", messages=messages)
    assistant_content = completion.choices[0].message["content"].strip()
    messages.append({"role": "assistant", "content": assistant_content})

    loading_label.config(text="")

    if user_content:
        role = "User"
        color = "#e9f7ef"
    else:
        role = "Minting"
        color = "#f0f0f0"

    chat_history.insert(tk.END, f"\n")
    chat_history.window_create(tk.END, window=SpeechBubble(Nickname, user_content, color))
    chat_history.insert(tk.END, "\n")
    chat_history.window_create(tk.END, window=SpeechBubble("Minting", assistant_content, "#f0f0f0"))
    chat_history.insert(tk.END, "\n")

    input_box.delete("1.0", tk.END)

    # Store assistant's response in data list
    data.append(assistant_content)

    root.after(10, scroll_to_bottom)

def scroll_to_bottom():
    if loading_label:
            loading_label.lift()
            loading_label.place_forget()
    chat_history.see(tk.END)

class SpeechBubble(tk.Frame):
    def __init__(self, role, text, bg_color, **kwargs):
        super().__init__(**kwargs)
        self.configure(bg=bg_color, padx=5, pady=5)

        label = tk.Label(self, text=text, wraplength=300, justify=tk.LEFT, bg=bg_color)
        label.pack()

        role_label = tk.Label(self, text=role, fg="gray40", bg=bg_color)
        role_label.pack(anchor=tk.W)

root.title("민팅에게 물어봐!")
root.configure(bg="#d8f3dc")

custom_font = font.Font(size=12)

chat_history = scrolledtext.ScrolledText(root, width=58, height=30, font=custom_font)
chat_history.pack(pady=10)

input_box = tk.Text(root, height=3, width=55, font=custom_font)
input_box.pack()

send_button = tk.Button(root, text="전송", command=send_message, font=custom_font)
send_button.pack(pady=10)

loading_label = tk.Label(root, text="", font=("Pacifico", 15), bg="white")

# Initial message
chat_history.tag_configure("Minting", background="#f0f0f0")
chat_history.insert(tk.END, "AI(MINTING)가 대답해주는 대화창입니다. 질문을 하실때 명확한 질문을 하실때 정확한 답변을 얻으실 수 있습니다.\n\n")

loading_label.place(relx=0.5, rely=0.5, anchor=tk.CENTER)

root.mainloop()

print(data)
