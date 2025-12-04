// src/main/java/com/pfe/dataops/dataopsapi/runs/WorkflowEdge.java
package com.pfe.dataops.dataopsapi.runs;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workflow_edges")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class WorkflowEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Job source : quand CE job termine...
     * ex: "run_article"
     */
    @Column(nullable = false, length = 200)
    private String fromJob;

    /**
     * Job cible : ...alors lancer CE job
     * ex: "run_commande"
     */
    @Column(nullable = false, length = 200)
    private String toJob;

    /**
     * Actif ou non (pour désactiver un lien sans le supprimer)
     */
    @Column(nullable = false)
    private boolean enabled = true;

    /**
     * Optionnel: lancer seulement si le run est SUCCESS
     * (tu peux étendre plus tard avec d’autres conditions)
     */
    @Column(nullable = false)
    private boolean onSuccessOnly = true;
}

