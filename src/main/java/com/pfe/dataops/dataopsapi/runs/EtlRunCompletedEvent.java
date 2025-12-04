package com.pfe.dataops.dataopsapi.runs;

import com.pfe.dataops.dataopsapi.backend.etl.EtlRun;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EtlRunCompletedEvent extends ApplicationEvent {

    private final EtlRun run;

    public EtlRunCompletedEvent(Object source, EtlRun run) {
        super(source);
        this.run = run;
    }
}
