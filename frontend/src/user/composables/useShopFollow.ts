/**
 * 店铺关注管理
 */

import { ref } from 'vue'

export function useShopFollow() {
  const isFollowing = ref(false)

  const toggleFollow = async () => {
    isFollowing.value = !isFollowing.value
    return isFollowing.value
  }

  return {
    isFollowing,
    toggleFollow
  }
}
