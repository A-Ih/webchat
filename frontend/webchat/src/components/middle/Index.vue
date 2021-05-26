<template>
    <div>
        <div class="index form-box" v-if="user">
            <div class="header">Write Message</div>
            <div class="body">
                <form @submit.prevent="onWriteMessage">
                    <div class="field">
                        <div class="name">
                            <label for="addresseeId">Id of addressee message</label>
                        </div>
                        <div class="value">
                            <input id="addresseeId" name="addresseeId" v-model="addresseeId"/>
                        </div>
                    </div>
                    <div class="field">
                        <div class="name">
                            <label for="text">Text</label>
                        </div>
                        <div class="value">
                            <textarea id="text" name="text" v-model="text"/>
                        </div>
                    </div>
                    <div class="field error" style="color: red;">{{ error }}</div>
                    <div class="button-field">
                        <input type="submit" value="Write Message">
                    </div>
                </form>
            </div>
        </div>
        <div v-for="message in getNewMessages()" :key="message.id">
            <Message :message="message" :user="user" :isNew="true"></Message>
        </div>
        <div v-for="message in getOldMessages()" :key="message.id">
            <Message :message="message" :user="user"></Message>
        </div>
    </div>
</template>

<script>
    import axios from "axios";
    import Message from "@/components/middle/Message";
    export default {
        name: "Index",
        components: {
            Message
        },
        props: ["user"],
        data: function () {
            return {
                text: "",
                addresseeId: "",
                error: "",
                polling: null,
                messages: []
            }
        },
        methods: {
            getFirstOwn: function() {
                return this.messages.findIndex(message => message.user.id === this.user.id);
            },
            getNewMessages: function() {
                return !this.user ? [] : this.messages.slice(0, this.getFirstOwn());
            },
            getOldMessages: function() {
                return !this.user ? this.messages : this.messages.slice(this.getFirstOwn);
            },
            onWriteMessage: function() {
                this.error = "";
                this.$root.$emit("onWriteMessage", this.text, this.addresseeId);
            },
            pollData: function() {
                this.polling = setInterval(() => {
                    axios.get("/api/1/messages").then(response => {
                        this.messages = response.data;
                    });
                }, 3000)
            }
        },
        beforeCreate() {
            this.$root.$on("onWriteMessageValidationError", (error) => this.error = error);
        },
        beforeDestroy() {
            clearInterval(this.polling);
        },
        created() {
            this.pollData();
        }
    }
</script>

<style scoped>

</style>