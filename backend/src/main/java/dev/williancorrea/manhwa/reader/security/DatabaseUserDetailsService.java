package dev.williancorrea.manhwa.reader.security;

import java.util.HashSet;
import java.util.Set;

import dev.williancorrea.manhwa.reader.features.access.group.AccessGroupPermissionRepository;
import dev.williancorrea.manhwa.reader.features.access.user.UserAccessGroupRepository;
import dev.williancorrea.manhwa.reader.features.access.user.UserRepository;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserAccessGroupRepository userAccessGroupRepository;
    private final AccessGroupPermissionRepository accessGroupPermissionRepository;
    private final MessageSource messageSource;

    public DatabaseUserDetailsService(
            UserRepository userRepository,
            UserAccessGroupRepository userAccessGroupRepository,
            AccessGroupPermissionRepository accessGroupPermissionRepository,
            MessageSource messageSource) {
        this.userRepository = userRepository;
        this.userAccessGroupRepository = userAccessGroupRepository;
        this.accessGroupPermissionRepository = accessGroupPermissionRepository;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        messageSource.getMessage("auth.error.user-not-found", null, LocaleContextHolder.getLocale())));

        var groupIds = userAccessGroupRepository.findAllByUser_Id(user.getId())
                .stream()
                .map(item -> item.getAccessGroup().getId())
                .toList();

        Set<GrantedAuthority> authorities = new HashSet<>();
        if (!groupIds.isEmpty()) {
            var permissions = accessGroupPermissionRepository.findAllByAccessGroup_IdIn(groupIds);
            for (var permission : permissions) {
                var name = permission.getPermission() != null ? permission.getPermission().getName() : null;
                if (name != null) {
                    authorities.add(new SimpleGrantedAuthority(name.name()));
                }
            }
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash() != null ? user.getPasswordHash() : "")
                .authorities(authorities)
                .build();
    }
}
