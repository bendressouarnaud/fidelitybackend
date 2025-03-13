package com.ankk.fidelity.httpbeans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BeanAuthentification {
    String mail, pwd, fcmtoken;
    int smartphonetype;
}