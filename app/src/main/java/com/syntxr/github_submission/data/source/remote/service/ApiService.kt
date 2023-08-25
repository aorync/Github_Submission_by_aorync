package com.syntxr.github_submission.data.source.remote.service

import com.syntxr.github_submission.data.source.remote.response.detail.UserDetailResponse
import com.syntxr.github_submission.data.source.remote.response.search.SearchResponse
import com.syntxr.github_submission.data.source.remote.response.user.UserResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    suspend fun getListUser(
    ) : UserResponse

    @GET("search/users")
    suspend fun searchUserByName(
        @Query("q") q : String,
    ) : SearchResponse

    @GET("users/{username}")
    suspend fun getUserDetail(
        @Path("username") username : String,
    ) : UserDetailResponse

    @GET("users/{username}/followers")
    suspend fun getUserFollowers(
        @Path("username") username: String,
    ) : UserResponse

    @GET("users/{username}/following")
    suspend fun getUserFollowing(
        @Path("username") username: String,
    ) : UserResponse

}