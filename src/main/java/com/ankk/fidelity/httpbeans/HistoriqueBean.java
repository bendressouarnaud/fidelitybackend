package com.ankk.fidelity.httpbeans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoriqueBean {
    private int id;
    private String contenu;
    private long temps;
}
