<template>
<!--    message.addressee.user.id === user.id-->
    <div class="message" v-bind:class="{
        'new-message': isNew,
        'reply': isReply()
    }" v-if="user">
        <div class="meta">
            <aside class="message-id">Message id: {{ message.id }}</aside>
            <div class="author">By: {{ message.user.login }}</div>
        </div>
        <div class="text">{{ message.text }}</div>
        <div class="addressee" v-if="message.addressee">
            To: {{ message.addressee.user.login }} (Message Id: {{ message.addressee.id }})
        </div>
    </div>
</template>

<script>
    export default {
        name: "Message",
        props: ["message", "user", "isNew"],
        methods: {
            isReply() {
                return this.user
                    && this.message.addressee
                    && this.message.addressee.user
                    && this.message.addressee.user.id === this.user.id;
            }
        }
    }
</script>

<style scoped>

</style>