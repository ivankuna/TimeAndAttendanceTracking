document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('togglePasswordVisibility').addEventListener('click', function() {
        var passwordInput = document.getElementById('password');
        var icon = document.getElementById('togglePasswordIcon');

        if (passwordInput.type === 'password') {
            passwordInput.type = 'text';
            icon.innerHTML = '<i class="far fa-eye-slash fa-1x"></i>';
        } else {
            passwordInput.type = 'password';
            icon.innerHTML = '<i class="far fa-eye fa-1x"></i>';
        }
    });
});