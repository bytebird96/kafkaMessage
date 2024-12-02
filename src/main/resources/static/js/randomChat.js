// WebSocket 연결 후 자동으로 랜덤 채팅 시작
const socket = new SockJS('/kafkaTest/chat');  // WebSocket 서버에 연결
const stompClient = Stomp.over(socket); // STOMP 프로토콜 사용

// 사용자 고유 ID (세션 또는 로컬스토리지에 저장해서 사용)
let userId = sessionStorage.getItem('userId') || `user_${Math.random().toString(36).substring(2, 9)}`;
sessionStorage.setItem('userId', userId);

// WebSocket 연결 후 자동으로 랜덤 채팅 시작
stompClient.connect({}, () => {
    console.log("STOMP 연결 성공");

    // 랜덤 매칭 요청 (매칭 시작)
    stompClient.send("/app/startRandomChat", {}, JSON.stringify({ userId: userId }));
    console.log("랜덤 채팅 요청 전송");

    // 매칭 상태 표시 (채팅 화면에 메시지 출력)
    const chatMessages = document.getElementById('chatMessages');
    let matchingStatus = document.getElementById('matchingStatus');
    if (!matchingStatus) {
        matchingStatus = document.createElement('div');
        matchingStatus.id = 'matchingStatus';
        matchingStatus.classList.add('system-message', 'center-align');
        matchingStatus.textContent = "매칭 대기 중...";
        chatMessages.appendChild(matchingStatus);
        chatMessages.scrollTop = chatMessages.scrollHeight;  // 새로운 메시지가 아래에 표시되도록
    }

    // 랜덤 채팅 매칭 시작
    stompClient.subscribe(`/user/${userId}/topic/matching`, function (message) {
        const chatMessage = JSON.parse(message.body);
        console.log("채팅 메시지:", chatMessage);

        // 기존 매칭 상태 메시지 삭제 (매칭 성공 시)
        if (chatMessage.content === "매칭 성공!") {
            if (matchingStatus) {
                matchingStatus.remove();  // 매칭 성공 후 '매칭 대기 중...' 삭제
                matchingStatus = null;    // 상태 메시지 참조 해제
            }

            // 매칭 성공 메시지 표시
            const matchedMessage = document.createElement('div');
            matchedMessage.classList.add('system-message', 'center-align');
            matchedMessage.textContent = "매칭 완료!";
            chatMessages.appendChild(matchedMessage);
            chatMessages.scrollTop = chatMessages.scrollHeight;

            // 채팅 입력란 활성화
            const messageInput = document.getElementById('messageInput');
            const sendButton = document.getElementById('send');
            messageInput.disabled = false;
            sendButton.disabled = false;
        }
    });

    // 채팅 메시지 수신 처리
    stompClient.subscribe("/topic/chat", function (message) {
        const chatMessage = JSON.parse(message.body);
        console.log("수신된 메시지:", chatMessage);

        const messageWrapper = document.createElement('div'); // 각 메시지의 wrapper div
        const newMessage = document.createElement('div');
        newMessage.classList.add('chat-message');

        if (chatMessage.sender === userId) {
            messageWrapper.classList.add('message-wrapper', 'right-align'); // 자신이 보낸 메시지 스타일 및 오른쪽 정렬
            newMessage.classList.add('my-message');
            newMessage.textContent = `${chatMessage.content}`;
        } else {
            messageWrapper.classList.add('message-wrapper', 'left-align'); // 상대방이 보낸 메시지 스타일 및 왼쪽 정렬
            newMessage.classList.add('other-message');
            newMessage.textContent = `${chatMessage.content}`;
        }

        messageWrapper.appendChild(newMessage);
        chatMessages.appendChild(messageWrapper);
        chatMessages.scrollTop = chatMessages.scrollHeight; // 스크롤을 가장 아래로
    });
}, (error) => {
    console.error("STOMP 연결 실패:", error);
});

// 세션이 끊어지거나 새로고침 후 처리
window.onbeforeunload = () => {
    // 세션 종료 시 userId 삭제
    sessionStorage.removeItem('userId');
};

// 새로고침 후 /randomChat으로 리다이렉트
if (!sessionStorage.getItem('userId')) {
    window.location.href = "/kafkaTest/view/randomChat";  // userId가 없으면 랜덤채팅 페이지로 이동
}

// 메시지 전송 로직
$(document).ready(function() {
    $('#send').click(function(event) {
        event.preventDefault();  // 기본 버튼 클릭 동작 방지 (새로고침 방지)

        const message = $('#messageInput').val();
        if (message) {
            // 메시지 전송 경로와 매칭 요청 경로를 분리
            stompClient.send("/app/sendMessage", {}, JSON.stringify({
                userId: userId,
                sender: userId,       // 사용자 ID
                content: message      // 메시지 내용
            }));
            console.log("메시지 전송:", message);

            // 전송 후 입력창 초기화
            $('#messageInput').val("");
        }
    });

    // Enter 키로 메시지 전송 가능하도록 추가
    $('#messageInput').keypress(function (e) {
        if (e.which === 13 && !$('#send').prop('disabled')) {
            e.preventDefault();  // 기본 Enter 키 동작 방지 (새로고침 방지)
            $('#send').click();
            return false;
        }
    });
});
