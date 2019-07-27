package com.las.squirrel;

import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.UntypedStateMachine;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;
import org.squirrelframework.foundation.fsm.annotation.StateMachineParameters;
import org.squirrelframework.foundation.fsm.impl.AbstractUntypedStateMachine;

/**
 * @author las
 * @date 18-10-12
 */
public class QuickStartSample {

    // 1. Define State Machine Event
    enum FSMEvent {
        ToA, ToB, ToC, ToD
    }

    enum State {
        A, B, C, D
    }

    // 2. Define State Machine Class
    @StateMachineParameters(stateType = State.class, eventType = FSMEvent.class, contextType = Integer.class)
    static class StateMachineSample extends AbstractUntypedStateMachine {
        protected void fromAToB(State from, State to, FSMEvent event, Integer context) {
            System.out.println("Transition from '" + from + "' to '" + to + "' on event '" + event +
                    "' with context '" + context + "'.");
        }

        protected void ontoB(State from, State to, FSMEvent event, Integer context) {
            System.out.println("Entry State \'" + to + "\'.");
        }
    }


    public static void main(String[] args) {
        // 3. Build State Transitions
        UntypedStateMachineBuilder builder = StateMachineBuilderFactory.create(StateMachineSample.class);
        builder.externalTransition().from(State.A).to(State.B).on(FSMEvent.ToB).callMethod("fromAToB");
        builder.onEntry(State.B).callMethod("ontoB");

        // 4. Use State Machine
        UntypedStateMachine fsm = builder.newStateMachine(State.A);
        fsm.fire(FSMEvent.ToB, 10);

        System.out.println("Current state is " + fsm.getCurrentState());
    }

}
