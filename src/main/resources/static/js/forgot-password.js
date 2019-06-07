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

form = document.getElementById('form');

form.addEventListener('submit', (event) => {

    event.preventDefault();

    if (emailValid()) {
        form.submit();
        return true;
    } else {
        event.preventDefault();
        return false;
    }
})