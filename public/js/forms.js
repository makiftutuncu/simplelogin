function validateForm(form, username, password)
{
    // Check validity of form fields
    re = /^\w+$/;

    if(username.value == '' || password.value == '')
    {
        alert("Username and password cannot be empty.");
        username.focus();

        return false;
    }
    else if(username.value.length < 3 || username.value.length > 24)
    {
        alert("Username should be at least 3 characters long.");
        username.focus();

        return false;
    }
    else if(!re.test(form.username.value))
    { 
        alert("Username must contain only letters, numbers and underscores.");
        username.focus();

        return false; 
    }
    else if(password.value.length < 6 || password.value.length > 32)
    {
        alert("Password should be at least 6 characters long.");
        password.focus();
        
        return false;
    }

    // Make sure the plaintext password doesn't get sent, instead hashed value will be sent.
    password.value = hex_sha512(password.value);;
 
    // Finally submit the form. 
    form.submit();

    return true;
}