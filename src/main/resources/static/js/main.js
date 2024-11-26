/**
 * main.js
 * 방 생성 및 1:1 랜덤 채팅 스크립트
 */

/**
 * 고유 ID 생성 함수
 * 1대1 대화 시 매칭에 사용
 * @returns {string}
 */

function generateUniqueId() {
    return 'user-' + Math.random().toString(36).substr(2, 9); // 랜덤한 고유 ID 생성
}

$(document).ready(function() {
    // sessionStorage에서 고유 ID 확인
    let userId = sessionStorage.getItem('userId');

    // 고유 ID가 없으면 새로 생성하여 sessionStorage에 저장
    if (!userId) {
        userId = generateUniqueId();
        sessionStorage.setItem('userId', userId);
    }

    console.log('현재 고유 ID:', userId);

    // 방 생성 버튼 클릭 시
    $('#createRoom').click(function() {
        window.location.href = '/kafkaTest/view/createRoom';  // 페이지 리디렉션
    });

    // 1:1 랜덤 채팅 버튼 클릭 시
    $('#createChat').click(function() {
        window.location.href = '/kafkaTest/view/randomChat';  // 1:1 랜덤 채팅 페이지로 이동
    });

    // // 서버에서 채팅방 목록 가져와서 동적으로 채워주기 (예시)
    // $.ajax({
    //     url: '/api/getChatRooms', // 서버에서 채팅방 목록을 가져오는 API
    //     method: 'GET',
    //     success: function(data) {
    //         // 예시: 서버에서 받은 채팅방 목록을 동적으로 표시
    //         data.rooms.forEach(function(room) {
    //             $('#chatRooms ul').append('<li>' + room.name + '</li>');
    //         });
    //     },
    //     error: function(error) {
    //         console.error('채팅방 목록을 가져오는 데 실패했습니다:', error);
    //     }
    // });
});
