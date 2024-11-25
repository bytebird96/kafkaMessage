// randomChat.js
const socket = new SockJS('/chat');  // WebSocket 서버에 연결
const stompClient = Stomp.over(socket); // STOMP 프로토콜 사용

// 사용자 고유 ID (세션 또는 로컬스토리지에 저장해서 사용)
let userId = sessionStorage.getItem('userId');

startRandomChat(); // 랜덤 채팅을 시작 요청


// 랜덤 채팅 시작 함수
function startRandomChat() {
    //연결된 다른 사용자와 매칭 요청
    stompClient.send("/app/startRandomChat", {}, JSON.stringify({ userId: userId }));
    console.log('연결된 다른 사용자와 매칭 start');
}
