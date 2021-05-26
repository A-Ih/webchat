<template>
  <div id="app">
    <Header :user="user"/>
    <Middle :user="user"/>
    <Footer/>
  </div>
</template>

<script>
import Header from '@/components/Header.vue';
import Middle from '@/components/Middle.vue';
import Footer from '@/components/Footer.vue';
import axios from "axios";

export default {
  name: 'App',
  components: {
    Header,
    Middle,
    Footer
  },
  data: function () {
    return {
      user: null,
      users: []
    }
  },
  beforeMount() {
    if (localStorage.getItem("jwt") && !this.user) {
      this.$root.$emit("onJwt", localStorage.getItem("jwt"));
    }

    axios.get("/api/1/users", {}).then(response => {
      this.users = response.data;
    });

  },
  beforeCreate() {
    this.$root.$on("onRegister", (login, name, password) => {
      if (!login || login.length < 3 || login.length > 16 || !/^[a-z]+$/.test(login)) {
        this.$root.$emit("onRegisterValidationError",
                "Login must consist of 3-16 lowercase latin letters");
      } else if (!name || name.length < 1 || name.length > 32) {
        this.$root.$emit("onRegisterValidationError", "Name must be 1-32 characters long");
      } else if (!password || password.length < 1 || password.length > 60) {
        this.$root.$emit("onRegisterValidationError", "Password must be 1-60 characters long");
      } else {
        axios.post("/api/1/users", {
          login, name, password
        }).then(() => {
          this.$root.$emit("onChangePage", "Enter");
        }).catch(error => {
          this.$root.$emit("onRegisterValidationError", error.response.data);
        });
      }
    });

    this.$root.$on("onEnter", (login, password) => {
      if (password === "") {
        this.$root.$emit("onEnterValidationError", "Password is required");
        return;
      }

      axios.post("/api/1/jwt", {
        login, password
      }).then(response => {
        localStorage.setItem("jwt", response.data);
        this.$root.$emit("onJwt", response.data);
      }).catch(error => {
        this.$root.$emit("onEnterValidationError", error.response.data);
      });
    });

    this.$root.$on("onJwt", (jwt) => {
      localStorage.setItem("jwt", jwt);

      axios.get("/api/1/users/auth", {
        params: {
          jwt
        }
      }).then(response => {
        this.user = response.data;
        this.$root.$emit("onChangePage", "Index");
      }).catch(() => this.$root.$emit("onLogout"))
    });

    this.$root.$on("onLogout", () => {
      localStorage.removeItem("jwt");
      this.user = null;
    });

    this.$root.$on("onWriteMessage", (text, addresseeId) => {
      if (this.user) {
        if (!text || text.length < 1 || text.length > 10000) {
          this.$root.$emit("onWriteMessageValidationError", "Text must be 1-10000 characters long");
        } else {
          // must send jwt in axios to get verified
          const jwt = localStorage.getItem("jwt");
          axios.post("/api/1/writeMessage", {
            jwt, text, "addresseeId" : addresseeId
          }).then(() => {}).catch(error => {
            this.$root.$emit("onWriteMessageValidationError", error.response.data);
          });
        }
      } else {
        this.$root.$emit("onWriteMessageValidationError", "No access");
      }
    });
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
