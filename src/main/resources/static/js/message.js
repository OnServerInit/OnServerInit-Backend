var currentConversation = [];
var currentConversationId = null;

var url = window.location.origin + '/';

function loadConversations(){
    $.ajax({
        url: url + 'message/api/get_groups',
        type: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        data: {},
        success: function(data){
            var groups = data;
            var i = 0;
            var loop = true;
            clearConversations();
            while(loop){
                if(groups['Group.' + i] != null){
                    var group = JSON.parse(groups['Group.' + i]);
                    createGroupElement(group);
                    i++;
                }else{
                    loop = false;
                }
            }

            if(i == 0){
                document.getElementById('starter-message').innerHTML = 'You have no active conversations, start a new one to begin chatting!';
            }else if(currentConversationId == null){
                document.getElementById('starter-message').innerHTML = 'You have no conversation selected, select one!';
            }
        }
    })
}

function loadConversation(id){
    currentConversation = [];
    if(id == null) return;
    document.getElementById('edit-btn').style.display = 'block';
    document.getElementById('action-btns').style.display = 'block';
    clearMessages()
    $.ajax({
        url: url + 'message/api/get_messages',
        type: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        data: {
            groupId: id
        },
        success: function(data){
            var conversation = data;
            var i = 0;
            var loop = true;
            while(loop){
                if(conversation['Message.' + i] != null){
                    if(currentConversation['Message.' + i] == null){
                        var message = JSON.parse(conversation['Message.' + i]);
                        var lastMessage = (conversation['Message.' + (i-1)] != undefined) ? JSON.parse(conversation['Message.' + (i - 1)]) : null;
                        createMessageElement(message, lastMessage);
                        newMessageScroll();
                    }
                    i++;
                }else{
                    loop = false;
                }
            }

            currentConversation = conversation;
        }
    });

    $.ajax({
        url: url + 'message/api/get_group',
        type: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        data: {
            groupId: id
        },
        success: function(data){
            $('#group-name').text(data['GroupName']);
        }
    })
}

function getMessages(){
    var id = currentConversationId;
    if(id == null) return;
    $.ajax({
        url: url + 'message/api/get_messages',
        type: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        data: {
            groupId: id
        },
        success: function(data){
            var conversation = data;
            var i = 0;
            var loop = true;
            while(loop){
                if(conversation['Message.' + i] != null){
                    if(currentConversation['Message.' + i] == null){
                        var message = JSON.parse(conversation['Message.' + i]);
                        var lastMessage = (conversation['Message.' + (i-1)] != undefined) ? JSON.parse(conversation['Message.' + (i - 1)]) : null;
                        createMessageElement(message, lastMessage);
                        newMessageScroll();
                    }
                    i++;
                }else{
                    loop = false;
                }
            }

            currentConversation = conversation;
        }
    });
}

function sendMessage(message){
    $.ajax({
        url: url + 'message/api/send_message',
        type: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        data: {
            content: message,
            groupId: currentConversationId
        },
        success: function(data){

        }
    }); 
}

function addUserToGroup(username){
    $.ajax({
        url: url + 'message/api/add_member',
        type: 'POST',
        headers: {'Content-Type': 'application/x-www-form-urlencoded'},
        data: {
            userId: username,
            groupId: currentConversationId
        },
        success: function(data){
            loadConversation(currentConversationId);
        }
    });
}

window.onload = function(){
    loadConversations();
}

loadConversation(currentConversationId);

setInterval(function(){
    getMessages();
}, 1000);

setInterval(function(){
    loadConversations();
}, 10000);

document.getElementById('save-btn').style.display = 'none';
document.getElementById('edit-btn').style.display = 'none';
document.getElementById('group-name-input').style.display = 'none';
document.getElementById('action-btns').style.display = 'none';


/* 

------[Dom Utilities]------

*/

function createGroupElement(groupJson){
    var group_container = document.getElementById('group_container');
    var group_element = document.createElement('div');
    group_element.className = 'conversation';
    group_element.setAttribute('groupId', groupJson['GroupId']);
    group_element.onclick = function(e){
        currentConversationId = (e.target.tagName == 'H3' || e.target.tagName == 'P') ? e.target.parentNode.getAttribute('groupId') : e.target.getAttribute('groupId');
        loadConversation(currentConversationId);
    }

    var group_name = document.createElement('h3');
    group_name.innerHTML = groupJson.GroupName;
    group_element.appendChild(group_name);

    var group_members = document.createElement('p');
    group_members.innerHTML = groupJson.GroupMembers + ' Members';
    group_element.appendChild(group_members);

    group_container.appendChild(group_element);
}

function createMessageElement(messageJson, lastMessageJson){
    var chat_container = document.getElementById('chat-container');
    var message_element = document.createElement('div');
    message_element.classList.add('message-container');

    if(messageJson['MessageAuthor'] == 'system'){
        var message = document.createElement('div');
        message.className = 'starting-message message';
        var content = document.createElement('h3');
        content.innerHTML = messageJson['MessageContent'];
        message.appendChild(content);
        message_element.appendChild(message);
        chat_container.appendChild(message_element);
        return;
    }

    var message_author = document.createElement('p');

    if(lastMessageJson == null || lastMessageJson['MessageAuthorName'] != messageJson['MessageAuthorName']){
        message_author.innerHTML = messageJson['MessageAuthorName'];
        message_author.className = 'message-author';
        if(messageJson['MessageAuthor'] == 'self'){
            message_author.classList.add('self');
        }
        message_element.appendChild(message_author);
    }

    var message_flex_container = document.createElement('div');
    message_flex_container.className = 'flex';
    (messageJson['MessageAuthor'] == 'self') ? message_flex_container.style.flexDirection = 'row-reverse' : message_flex_container.style.flexDirection = 'row';

    var profile_picture = document.createElement('img');
    profile_picture.className = 'profile-picture';
    profile_picture.src = url + 'profile-picture/' + messageJson['MessageAuthorId'];
    (messageJson['MessageAuthor'] == 'self') ? profile_picture.style.marginLeft = '0.5rem' : profile_picture.style.marginRight = '0.5rem';
    if(lastMessageJson == null || lastMessageJson['MessageAuthorName'] != messageJson['MessageAuthorName']){
        message_flex_container.appendChild(profile_picture);
    }

    var message_div = document.createElement('div');
    message_div.className = 'message ' + messageJson['MessageAuthor'];
    if(lastMessageJson == null || lastMessageJson['MessageAuthorName'] == messageJson['MessageAuthorName']){
        (messageJson['MessageAuthor'] != 'self') ? message_div.style.marginLeft = '3rem' : message_div.style.marginRight = '3rem';
    }
    message_flex_container.appendChild(message_div);

    message_element.appendChild(message_flex_container);

    var message_content = document.createElement('p');
    var message_content_text = messageJson['MessageContent'];
    message_content_text = decodeURIComponent(message_content_text);
    message_content_text = message_content_text.replace(/\+/g, ' ');
    message_content.innerHTML = message_content_text;
    message_div.appendChild(message_content);

    (messageJson['MessageAuthor'] == 'self') ? message_element.style.marginLeft = '100%' : message_element.style.marginLeft = '-100%';

    chat_container.appendChild(message_element);

    setTimeout(function(){
        message_element.style.marginLeft = '0';
        message_element.style.marginLeft = '0';
    }, 1);
}

function clearMessages(){
    var chat_container = document.getElementById('chat-container');
    // for(var i = chat_container.children.length - 1; i >= 0; i--){
    //     var message = chat_container.children[i];
    //     if(message.children[0].children[0].classList.contains('self')){
    //         message.style.marginLeft = '100%';
    //     }else{
    //         message.style.marginLeft = '-100%';
    //     }
    // }

    // setTimeout(function(){
        // chat_container.removeChild(message);
    // }, 500);
    chat_container.innerHTML = '';
}

function clearConversations(){
    var group_container = document.getElementById('group_container');
    group_container.innerHTML = '';
}

function addUserSearchResults(name){
    var p = document.createElement('p');
    p.innerText = name;
    p.addEventListener('click', function(){
        addUserToGroup(name);
    });

    var search_results = document.getElementById('search-results');
    search_results.appendChild(p);
}

function closeSearch(){
    var search_results = document.getElementById('search-results');
    search_results.innerHTML = '';
    document.getElementById('user-add-body').style.top = '120%';
    setTimeout(function(){
        document.getElementById('user-add').style.display = 'none';
    }, 500);
}

function showSearch(){
    document.getElementById('user-search-input').value = '';
    document.getElementById('user-add').style.display = 'block';
    setTimeout(function(){
        document.getElementById('user-add-body').style.top = '50%';
    }, 1);
}

function leaveGroup(){
    $.ajax({
        url: url + 'message/api/leave',
        type: 'POST',
        data: {
            groupId: currentConversationId
        },
        success: function(data){
            if(data == 'success'){
                loadGroups();
                loadConversation(null);
            }
        }
    })
}

/*

------[Event Listeners]------

*/

document.getElementById('chat-send').addEventListener('click', function(){
    var message = document.getElementById('chat-input').value;
    sendMessage(message);
    document.getElementById('chat-input').value = '';
});

document.getElementById('chat-input').addEventListener('keypress', function(e){
    if(e.keyCode == 13){
        var message = document.getElementById('chat-input').value;
        sendMessage(message);
        document.getElementById('chat-input').value = '';
    }
});


document.getElementById('user-add').style.display = 'none';
document.getElementById('user-add-body').style.top = '120%';
document.getElementById('user-add').addEventListener('click', function(e){
    // make sure button isnt on user-add-body
    var rect = document.getElementById('user-add-body').getBoundingClientRect();
    if(e.clientX > rect.left && e.clientX < rect.right && e.clientY > rect.top && e.clientY < rect.bottom){
        return;
    }
    closeSearch();
});

document.getElementById('add-user').addEventListener('click', function(){
    showSearch();
});

document.getElementById('leave').addEventListener('click', function(){
    leaveGroup();
});

document.getElementById('user-search-input').addEventListener('keypress', function(e){
    var text = document.getElementById('user-search-input').value;
    if(text.length > 2){
        $.ajax({
            url: url + 'message/api/fuzzy_search/users',
            type: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: {
                searchTerm: text
            },
            success: function(data){
                var users = data;
                users = users['usernames'].split(',');
                document.getElementById('search-results').innerHTML = '';
                for(var i = 0; i < users.length; i++){
                    addUserSearchResults(users[i]);
                }
            }
        });
    }
});

document.getElementById('edit-btn').addEventListener('click', function(){
    document.getElementById('edit-btn').style.display = 'none';
    document.getElementById('save-btn').style.display = 'block';
    var input = document.getElementById('group-name-input');
    input.style.display = 'block';
    input.focus();
    input.value = document.getElementById('group-name').innerText;
    document.getElementById('group-name').style.display = 'none';
});

document.getElementById('save-btn').addEventListener('click', function(){
    var input = document.getElementById('group-name-input');
    var name = input.value;
    if(name.length > 0){
        $.ajax({
            url: url + 'message/api/edit_group_name',
            type: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            data: {
                groupId: currentConversationId,
                name: name
            },
            success: function(data){
                document.getElementById('group-name').innerText = name;
                document.getElementById('group-name').style.display = 'block';
                document.getElementById('edit-btn').style.display = 'block';
                document.getElementById('save-btn').style.display = 'none';
                input.style.display = 'none';
            }
        });
    }
});

/*

------[Scroll Utilities]------

*/

function newMessageScroll(){
    var chat_container = document.getElementById('chat-container');
    // only scroll if they are at the bottom
    if(chat_container.scrollTop >= chat_container.scrollHeight - chat_container.clientHeight - 200){
        chat_container.scrollTop = chat_container.scrollHeight;
    }else{
        console.log(chat_container.scrollTop, chat_container.scrollHeight, chat_container.clientHeight, chat_container.scrollHeight - chat_container.clientHeight - 200);
    }
}