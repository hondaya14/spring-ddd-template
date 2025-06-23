package com.nqvno.springdddtemplate.api.service

import com.nqvno.springdddtemplate.domain.model.entity.ApiUser
import com.nqvno.springdddtemplate.domain.model.valueobject.id.ComponentId
import com.nqvno.springdddtemplate.domain.model.valueobject.id.FunctionId
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService(
    val apiKey: ApiKeyProperties,
) : AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {

    // Authentication
    @ConfigurationProperties(prefix = "api")
    data class ApiKeyProperties(
        val keys: Map<String, String>,
    ) {
        fun getComponentName(apiKey: String): String {
            val componentName: String? = keys.entries.find { it.value == apiKey }?.key
            // Unauthenticatedの場合, AuthenticationEntryPointが例外を拾う.
            return componentName ?: throw BadCredentialsException("Invalid API key")
        }
    }

    fun getApiUser(requestApiKey: String): ApiUser {
        return ApiUser.of(ComponentId.of(apiKey.getComponentName(requestApiKey)))
    }

    // Authorization
    override fun loadUserDetails(token: PreAuthenticatedAuthenticationToken): UserDetails {
        val apiUser: ApiUser = getApiUser(token.credentials.toString())
        val authorityList: List<GrantedAuthority> = AuthorityUtils.createAuthorityList(
            apiUser.authorityList.map(FunctionId::value)
        )

        // thread localにauthenticationを保存している.
        return User.builder()
            .username(apiUser.componentId.value)
            .password("") // apikey認証のためpasswordは設定しない
            .authorities(authorityList)
            .build()
    }

    // thread localにauthenticationから取得する
    fun getCurrentSystemId(): ComponentId {
        val user: User = SecurityContextHolder.getContext().authentication.principal as User
        return ComponentId.of(user.username)
    }
}
