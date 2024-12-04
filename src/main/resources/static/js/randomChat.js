// WebSocket 연결 후 자동으로 랜덤 채팅 시작
const socket = new SockJS('/kafkaTest/chat');  // SockJS를 사용해 WebSocket 서버에 연결
const stompClient = Stomp.over(socket);  // STOMP 프로토콜을 사용하여 WebSocket 연결 관리

// 사용자 고유 ID 설정 (세션 또는 로컬 스토리지에 저장하여 유지)
let userId = sessionStorage.getItem('userId') || `user_${Math.random().toString(36).substring(2, 9)}`;
sessionStorage.setItem('userId', userId);

// STOMP 연결을 설정한 후 랜덤 채팅 시작
stompClient.connect({}, () => {
    console.log("STOMP 연결 성공");

    // 랜덤 채팅 매칭 요청 메시지 전송
    stompClient.send("/app/startRandomChat", {}, JSON.stringify({ userId: userId }));
    console.log("랜덤 채팅 요청 전송");

    // 매칭 상태를 표시하기 위해 화면에 메시지를 출력
    const chatMessages = document.getElementById('chatMessages');
    let matchingStatus = document.getElementById('matchingStatus');
    if (!matchingStatus) {
        matchingStatus = document.createElement('div');
        matchingStatus.id = 'matchingStatus';
        matchingStatus.classList.add('system-message', 'center-align');
        matchingStatus.textContent = "매칭 대기 중...";
        chatMessages.appendChild(matchingStatus);
        chatMessages.scrollTop = chatMessages.scrollHeight;  // 새로운 메시지가 화면 아래에 표시되도록 스크롤 설정
    }

    // 매칭된 사용자를 구독하고, 매칭 결과를 처리
    stompClient.subscribe(`/user/${userId}/topic/matching`, function (message) {
        const chatMessage = JSON.parse(message.body);
        console.log("채팅 메시지:", chatMessage);

        // 매칭 성공 시 기존 매칭 대기 메시지 삭제
        if (chatMessage.content === "매칭 성공!") {
            if (matchingStatus) {
                matchingStatus.remove();  // 매칭 성공 후 '매칭 대기 중...' 메시지 삭제
                matchingStatus = null;    // 상태 메시지 참조 해제
            }

            // 매칭 성공 메시지를 화면에 표시
            const matchedMessage = document.createElement('div');
            matchedMessage.classList.add('system-message', 'center-align');
            matchedMessage.textContent = "매칭 완료!";
            chatMessages.appendChild(matchedMessage);
            chatMessages.scrollTop = chatMessages.scrollHeight;

            // 채팅 입력란과 전송 버튼 활성화
            const messageInput = document.getElementById('messageInput');
            const sendButton = document.getElementById('send');
            messageInput.disabled = false;
            sendButton.disabled = false;
        }
    });

    // 모든 채팅 메시지를 구독하여 화면에 표시
    stompClient.subscribe("/topic/chat", function (message) {
        const chatMessage = JSON.parse(message.body);
        console.log("수신된 메시지:", chatMessage);

        // 각 채팅 메시지에 대한 스타일과 위치 설정
        const messageWrapper = document.createElement('div');
        const newMessage = document.createElement('div');
        newMessage.classList.add('chat-message');

        // 자신이 보낸 메시지와 상대방의 메시지를 구분하여 스타일 적용
        if (chatMessage.sender === userId) {
            messageWrapper.classList.add('message-wrapper', 'right-align');  // 자신의 메시지는 오른쪽 정렬
            newMessage.classList.add('my-message');
            newMessage.textContent = `${chatMessage.content}`;
        } else {
            messageWrapper.classList.add('message-wrapper', 'left-align');  // 상대방 메시지는 왼쪽 정렬
            newMessage.classList.add('other-message');
            newMessage.textContent = `${chatMessage.content}`;
        }

        messageWrapper.appendChild(newMessage);
        chatMessages.appendChild(messageWrapper);
        chatMessages.scrollTop = chatMessages.scrollHeight;  // 스크롤을 가장 아래로 이동
    });
}, (error) => {
    console.error("STOMP 연결 실패:", error);
});

// 세션이 끊어지거나 새로고침 시 처리
window.onbeforeunload = () => {
    // 세션 종료 시 userId 삭제
    sessionStorage.removeItem('userId');
};

// 세션 정보가 없을 때 /randomChat으로 리다이렉트
if (!sessionStorage.getItem('userId')) {
    window.location.href = "/kafkaTest/view/randomChat";  // userId가 없으면 랜덤 채팅 페이지로 이동
}

// 메시지 전송 로직
$(document).ready(function() {
    $('#send').click(function(event) {
        event.preventDefault();  // 버튼 기본 동작(새로고침)을 방지

        const message = $('#messageInput').val();
        if (message) {
            // 사용자가 입력한 메시지를 서버로 전송
            stompClient.send("/app/sendMessage", {}, JSON.stringify({
                userId: userId,
                sender: userId,
                content: message
            }));
            console.log("메시지 전송:", message);

            // 전송 후 입력창 초기화
            $('#messageInput').val("");
        }
    });

    // Enter 키로도 메시지 전송이 가능하도록 설정
    $('#messageInput').keypress(function (e) {
        if (e.which === 13 && !$('#send').prop('disabled')) {
            e.preventDefault();  // 기본 Enter 동작 방지
            $('#send').click();  // 클릭 이벤트 발생
            return false;
        }
    });
});
