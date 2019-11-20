function show(event) {
    let button = document.getElementById(event.target.id);
    let input = button.parentElement.parentElement.children[0];
    if (input.type != "password") {
        input.type = "password";
        button.src = "/static/img/eye-def.svg";
    } else {
        input.type = "text";
        button.src = "/static/img/eye.svg";
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

form = document.getElementById('form');

// form.addEventListener('submit', (event) = > {
//
//     event.preventDefault();
//
// if (passValid()) {
//     form.submit();
//     return true;
// } else {
//     event.preventDefault();
//     return false;
// }
// })