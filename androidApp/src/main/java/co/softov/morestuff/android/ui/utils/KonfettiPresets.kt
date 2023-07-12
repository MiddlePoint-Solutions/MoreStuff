package co.softov.morestuff.android.ui.utils

import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.util.concurrent.TimeUnit

fun explode(): List<Party> {

    val party = Party(
        speed = 0f,
        maxSpeed = 50f,
        damping = 0.9f,
        angle = Angle.RIGHT - 45,
        spread = 90,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(80),
        position = Position.Relative(0.0, 0.4)
    )

    return listOf(
        party,
        party.copy(
            angle = party.angle - 90, // flip angle from right to left
            position = Position.Relative(1.0, 0.4)
        ),
    )
}

fun festive(): List<Party> {
    val party = Party(
        speed = 30f,
        maxSpeed = 50f,
        damping = 0.9f,
        angle = Angle.TOP,
        spread = 45,
        size = listOf(Size.SMALL, Size.LARGE),
        timeToLive = 3000L,
        rotation = Rotation(),
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 8, TimeUnit.MILLISECONDS).max(30),
        position = Position.Relative(0.5, 1.0)
    )

    return listOf(
        party,
        party.copy(
            speed = 55f,
            maxSpeed = 65f,
            spread = 10,
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(10),
        ),
        party.copy(
            speed = 50f,
            maxSpeed = 60f,
            spread = 120,
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(40),
        ),
        party.copy(
            speed = 65f,
            maxSpeed = 80f,
            spread = 10,
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(10),
        )
    )
}


fun parade(): List<Party> {
    val party = Party(
        speed = 0f,
        maxSpeed = 30f,
        damping = 0.5f,
        angle = Angle.RIGHT - 45,
        spread = Spread.WIDE,
        colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
        emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(30),
        position = Position.Relative(0.0, 0.5)
    )

    return listOf(
        party,
        party.copy(
            angle = party.angle - 90, // flip angle from right to left
            position = Position.Relative(1.0, 0.5)
        ),
    )
}

fun rain(): List<Party> {
    return listOf(
        Party(
            speed = 0f,
            maxSpeed = 15f,
            damping = 0.9f,
            angle = Angle.BOTTOM,
            spread = Spread.ROUND,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 1, TimeUnit.SECONDS).perSecond(20),
            position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
        )
    )
}