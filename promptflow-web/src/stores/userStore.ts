import { reactive } from 'vue'

interface UserState {
  token: string | null;
  username: string | null;
  remainingCount: number;
  totalUsedCount: number;
  isLoggedIn: boolean;
}

const userState = reactive<UserState>({
  token: localStorage.getItem('token'),
  username: localStorage.getItem('username'),
  remainingCount: Number(localStorage.getItem('remainingCount') || 0),
  totalUsedCount: Number(localStorage.getItem('totalUsedCount') || 0),
  isLoggedIn: !!localStorage.getItem('token')
})

const userStore = {
  state: userState,
  login(token: string, username: string, remainingCount?: number, totalUsedCount?: number) {
    userState.token = token
    userState.username = username
    userState.isLoggedIn = true
    if (remainingCount !== undefined) {
      userState.remainingCount = remainingCount
      localStorage.setItem('remainingCount', remainingCount.toString())
    }
    if (totalUsedCount !== undefined) {
      userState.totalUsedCount = totalUsedCount
      localStorage.setItem('totalUsedCount', totalUsedCount.toString())
    }
    localStorage.setItem('token', token)
    localStorage.setItem('username', username)
  },
  updateUserInfo(username: string, remainingCount: number, totalUsedCount: number) {
    userState.username = username
    userState.remainingCount = remainingCount
    userState.totalUsedCount = totalUsedCount
    localStorage.setItem('username', username)
    localStorage.setItem('remainingCount', remainingCount.toString())
    localStorage.setItem('totalUsedCount', totalUsedCount.toString())
  },
  logout() {
    userState.token = null
    userState.username = null
    userState.remainingCount = 0
    userState.totalUsedCount = 0
    userState.isLoggedIn = false
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('remainingCount')
    localStorage.removeItem('totalUsedCount')
  }
}

export default userStore
