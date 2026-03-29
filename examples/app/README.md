# Example App

This example application shows a variaty of use-cases for the plugin.
Most of the results are written to the subfolder `diagrams`.
They are considered as documented behaviour and therefore checked by the unit tests.
Two diagrams are actually inserted right here:

```plantuml
@startuml Packages only
!pragma useIntermediatePackages false

package io.gitlab.plunts.gradle.plantuml.examples {
  [app] as io.gitlab.plunts.gradle.plantuml.examples.app
  [dto] as io.gitlab.plunts.gradle.plantuml.examples.dto
}
package io.gitlab.plunts.gradle.plantuml.examples.app {
  [controller] as io.gitlab.plunts.gradle.plantuml.examples.app.controller
  [entity] as io.gitlab.plunts.gradle.plantuml.examples.app.entity
  [service] as io.gitlab.plunts.gradle.plantuml.examples.app.service
}
package io.gitlab.plunts.gradle.plantuml.examples.app.service {
  [internal] as io.gitlab.plunts.gradle.plantuml.examples.app.service.internal
}
io.gitlab.plunts.gradle.plantuml.examples.app.controller --> io.gitlab.plunts.gradle.plantuml.examples.app.service
io.gitlab.plunts.gradle.plantuml.examples.app.service --> io.gitlab.plunts.gradle.plantuml.examples.app.service.internal
@enduml
```

```plantuml
@startuml Write To Markdown
!pragma useIntermediatePackages false

class "Car" as io.gitlab.plunts.gradle.plantuml.examples.app.entity.Car {
  +int getMaxPassengers()
  +List<ParkingSlot> getAllowedParkingSlots()
  +void setMaxPassengers(int)
  +void setAllowedParkingSlots(List<ParkingSlot>)
}
entity "Truck" as io.gitlab.plunts.gradle.plantuml.examples.app.entity.Truck {
  +int getMaxLoad()
  +void setMaxLoad(int)
}
entity "Vehicle" as io.gitlab.plunts.gradle.plantuml.examples.app.entity.Vehicle {
  +String getPlateNo()
  +boolean isRegistered()
  +VehicleState getState()
  +List<Tire> getTires()
  +SteeringWheel getSteeringWheel()
  +List<Passenger> getPassengers()
  +List<Trip> getTrips()
  +void setPlateNo(String)
  +void setRegistered(boolean)
  +void setState(VehicleState)
  +void setTires(List<Tire>)
  +void setSteeringWheel(SteeringWheel)
  +void setPassengers(List<Passenger>)
  +void setTrips(List<Trip>)
}
io.gitlab.plunts.gradle.plantuml.examples.app.entity.Car -u-|> io.gitlab.plunts.gradle.plantuml.examples.app.entity.Vehicle
io.gitlab.plunts.gradle.plantuml.examples.app.entity.Truck -u-|> io.gitlab.plunts.gradle.plantuml.examples.app.entity.Vehicle
@enduml
```

Here is some completly unrelated diagram which is not overwritten:

```plantuml
@startuml
(*) --> "First Activity"
"First Activity" --> (*)
@enduml
```
