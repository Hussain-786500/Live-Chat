

// 3 third js function..........main run function....

let stompClient = null;
let user = null;
let users = [];

const socket = new SockJS('/ws-endpoints');
stompClient = Stomp.over(socket);

stompClient.connect({}, () => {
    console.log("Connected as" + user);

    // 👉 JOIN notify backend
        stompClient.send("/app/chat.addUser", {}, JSON.stringify({
            sender: user,
            type: "JOIN"
        }));

        // 👉 listen users list
        stompClient.subscribe("/topic/users", (msg) => {

            const data = JSON.parse(msg.body);

            if (!users.includes(data.sender)) {
                users.push(data.sender);
            }

            renderUsers();
        });


    // messages already working
    stompClient.subscribe('/topic/messages', (message) => {
        const msg = JSON.parse(message.body);

        const wrapper = document.createElement('li');
        wrapper.classList.add('message');

        const isMine = msg.sender === user;

        wrapper.classList.add(isMine ? 'right' : 'left');

        wrapper.innerHTML = `
            <div class="bubble">
                
                ${!isMine ? `<div class="avatar">${msg.sender[0].toUpperCase()}</div>` : ""}

                <div class="textBox">
                    <div class="text">${msg.content}</div>

                    <div class="meta">
                        ${msg.sender} • ${msg.timeStamp}
                        ${isMine ? " ✓✓" : ""}
                    </div>
                </div>
            </div>
        `;

        document.getElementById('messageList').appendChild(wrapper);

        // auto scroll
        const list = document.getElementById('messageList');
        list.scrollTop = list.scrollHeight;
    });
});

// SEND MESSAGE
function sendMessage() {

    const input = document.getElementById("messageInput");
    const message = input.value.trim();

    if (message === "") return;

    stompClient.send("/app/chat", {}, JSON.stringify({
        sender: user,
        content: message,
        type: "CHAT",
        timeStamp: new Date().toLocaleString('en-IN', {
            timeZone: 'Asia/Kolkata'
        })
    }));

    input.value = "";
}

function connect() {

    user = document.getElementById('senderInput').value;

    document.getElementById('loginDiv').style.display = 'none';
    document.getElementById('messageBlock').style.display = 'block';

    document.getElementById('userName').innerHTML =
    'You are logged in as : ➤ <span class="userText">' + user.toUpperCase() + '</span>';
}

// ENTER KEY SUPPORT (IMPORTANT)
document.getElementById("messageInput").addEventListener("keydown", function (event) {
    if (event.key === "Enter") {
        event.preventDefault();

        const input = document.getElementById("messageInput");

        if (input.value.trim() !== "") {
        sendMessage();
    }
}
});



