async function submitLogin() {
            event.preventDefault();

            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;
            console.log("submitting form");

            const response = await fetch("/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({ username, password })
            });

            console.log("Response Status:", response.status);
            console.log("Response Text:", await response.text());

            if (response.ok) {
                const token = await response.text();
                console.log("JWT Token:", token);
                alert("Login successful! Token: " + token);
            } else {
                document.getElementById("error-message").textContent = "Invalid username or password";
            }
        }