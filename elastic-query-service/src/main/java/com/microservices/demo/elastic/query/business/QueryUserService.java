package com.microservices.demo.elastic.query.business;


import com.microservices.demo.elastic.query.dataaccess.entity.UserPermission;

import java.util.List;
import java.util.Optional;

public interface QueryUserService {

    Optional<List<UserPermission>> findAllPermissionsByUsername(String username);
}
