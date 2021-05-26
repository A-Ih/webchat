<template>
    <header>
        <a href="#" @click.prevent="changePage('Index')">
            <img src="@/assets/logo.gif" alt="webchat" title="webchat">
        </a>
        <div class="enter-or-register-box">
        </div>
        <nav>
            <template v-if="user">
                {{ user.name }} a.k.a. {{ user.login }}
                |
                <a href="#" @click.prevent="onLogout">Logout</a>
            </template>
            <template v-else>
                <a href="#" @click.prevent="changePage('Enter')">Enter</a>
                |
                <a href="#" @click.prevent="changePage('Register')">Register</a>
            </template>
        </nav>
    </header>

</template>

<script>
export default {
    name: "Header",
    props: ["user"],
    methods: {
        changePage: function (page) {
            this.$root.$emit("onChangePage", page);
        },
        onLogout: function () {
            this.$root.$emit("onLogout");
            this.changePage("Index");
        }
    }
}
</script>

<style scoped>
    ul {
        align-content: center;
    }
</style>