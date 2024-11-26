// WebSocket 연결 후 자동으로 랜덤 채팅 시작
const socket = new SockJS('/kafkaTest/chat');  // WebSocket 서버에 연결
const stompClient = Stomp.over(socket); // STOMP 프로토콜 사용

// 사용자 고유 ID (세션 또는 로컬스토리지에 저장해서 사용)
let userId = sessionStorage.getItem('userId') || `user_${Math.random().toString(36).substring(2, 9)}`;
sessionStorage.setItem('userId', userId);

// WebSocket 연결 후 자동으로 랜덤 채팅 시작
stompClient.connect({}, () => {
    console.log("STOMP 연결 성공");

    // 랜덤 매칭 요청
    stompClient.send("/app/startRandomChat", {}, JSON.stringify({ userId: userId }));
    console.log("랜덤 채팅 요청 전송");

    // 매칭 상태 표시 (채팅 화면에 메시지 출력)
    const chatMessages = document.getElementById('chatMessages');
    const matchingStatus = document.createElement('div');
    matchingStatus.id = 'matchingStatus';
    matchingStatus.classList.add('system-message');
    matchingStatus.textContent = "매칭 대기 중...";
    chatMessages.appendChild(matchingStatus);
    chatMessages.scrollTop = chatMessages.scrollHeight;  // 새로운 메시지가 아래에 표시되도록

    // 채팅 메시지 수신
    stompClient.subscribe('/topic/chat', function (message) {
        const chatMessage = JSON.parse(message.body);
        console.log("채팅 메시지:", chatMessage);

        // 기존 매칭 상태 메시지 삭제 (매칭 성공 시)
        const existingStatus = document.getElementById('matchingStatus');
        if (existingStatus) {
            existingStatus.remove();  // 매칭 성공 후 '매칭 대기 중...' 삭제
        }

        // 채팅 메시지 표시 (메시지 유형에 따른 스타일 적용)
        const newMessage = document.createElement('div');
        newMessage.classList.add('chat-message');

        if (chatMessage.sender === '시스템') {
            newMessage.classList.add('system-message');
            newMessage.textContent = chatMessage.content;  // 시스템 메시지
        } else {
            newMessage.classList.add('user-message');
            newMessage.textContent = `${chatMessage.sender}: ${chatMessage.content}`;  // 사용자 메시지
        }

        chatMessages.appendChild(newMessage);
        chatMessages.scrollTop = chatMessages.scrollHeight;  // 새로운 메시지가 아래에 표시되도록

        // 매칭 완료 메시지가 오면 "매칭 완료!" 표시하고 채팅 입력을 활성화
        if (chatMessage.content === "매칭 성공!") {
            const matchedMessage = document.createElement('div');
            matchedMessage.classList.add('system-message');
            matchedMessage.textContent = "매칭 완료!";
            chatMessages.appendChild(matchedMessage);
            chatMessages.scrollTop = chatMessages.scrollHeight;  // 새로운 메시지가 아래에 표시되도록

            // 채팅 입력란 활성화
            const messageInput = document.getElementById('messageInput');
            const sendButton = document.getElementById('send');
            messageInput.disabled = false;  // 채팅 입력 활성화
            sendButton.disabled = false;  // 전송 버튼 활성화
        }
    });
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
