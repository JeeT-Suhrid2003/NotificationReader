from flask import Flask, request
import sqlite3

app = Flask(__name__)

# SQLite DB setup
conn = sqlite3.connect("notifications.db", check_same_thread=False)
cursor = conn.cursor()
cursor.execute('''
    CREATE TABLE IF NOT EXISTS notifications (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        message TEXT NOT NULL,
        timestamp INTEGER NOT NULL
    )
''')
conn.commit()

@app.route('/notifications', methods=['POST'])
def receive_notification():
    data = request.get_json()
    message = data.get("message", "")
    timestamp = data.get("timestamp", 0)
    print(f"Received: {message} at {timestamp}")

    cursor.execute("INSERT INTO notifications (message, timestamp) VALUES (?, ?)", (message, timestamp))
    conn.commit()
    return {"status": "success"}, 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
