function show(event) {
    let button = document.getElementById(event.target.id);
    let input = button.parentElement.previousElementSibling.children[0];
    if (input.type != 'password') {
        input.type = 'password';
        button.src = '/static/img/eye-def.svg';
    } else {
        input.type = 'text';
        button.src = '/static/img/eye.svg';
    }
}

function emailValid() {

    var email = document.getElementById('email').value;
    var err = document.getElementById('email').nextElementSibling;

    if (/[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$/.test(email)) {
        document.getElementById('email').style.borderColor = 'rgba(0,0,0,0.15)';
        err.style.display = 'none';
        return true;
    } else {
        document.getElementById('email').style.borderColor = 'red';
        err.style.display = 'block';
        return false;
    }
}

function passValid() {
    var password = document.getElementById('password').value;
    var err = document.getElementById('password').nextElementSibling;

    if (/(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{7,}/.test(password)) {
        document.getElementById('password').style.borderColor = 'rgba(0,0,0,0.15)';
        err.style.display = 'none';
        return true;
    } else {
        document.getElementById('password').style.borderColor = 'red';
        err.style.display = 'block';
        return false;
    }

}

function check() {
    var password = document.getElementById('password').value;
    var confirm = document.getElementById('confirm').value;
    var error = document.getElementById('confirm').nextElementSibling;

    if (confirm != password || confirm == '') {
        document.getElementById('confirm').style.borderColor = 'red';
        error.style.display = 'block';
        return false
    } else {
        error.style.display = 'none';
        document.getElementById('confirm').style.borderColor = 'rgba(0,0,0,0.15)';
        return true
    }
}

function firstNameValid() {
    var firstName = document.getElementById('firstname').value;
    var error = document.getElementById('firstname').nextElementSibling;

    if (firstName.length <= 1) {
        error.style.display = 'block';
        document.getElementById('firstname').style.borderColor = 'red';
        return false
    } else {
        error.style.display = 'none';
        document.getElementById('firstname').style.borderColor = 'rgba(0,0,0,0.15)';
        return true
    }

}

function lastNameValid() {
    var lastName = document.getElementById('lastname').value;
    var error = document.getElementById('lastname').nextElementSibling;

    if (lastName.length <= 1) {
        error.style.display = 'block';
        document.getElementById('lastname').style.borderColor = 'red';
        return false
    } else {
        error.style.display = 'none';
        document.getElementById('lastname').style.borderColor = 'rgba(0,0,0,0.15)';
        return true
    }

}

form = document.getElementById('form');

// form.addEventListener('submit', (event) = > {
//
//     event.preventDefault();
//
// if (emailValid() && passValid() && check() && firstNameValid() && lastNameValid()) {
//     form.submit();
//     return true;
// } else {
//     event.preventDefault();
//     return false;
// }
// })
