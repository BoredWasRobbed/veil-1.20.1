package net.bored.veil.ability.impl.technique;

import net.bored.veil.ability.Ability;
import net.bored.veil.ability.CursedTechnique;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Arrays;

/**
 * An example implementation of a Cursed Technique for the Kamo clan.
 */
public class BloodManipulation extends CursedTechnique {
    public BloodManipulation() {
        super("Blood Manipulation",
                Arrays.asList(
                        new Ability("Flowing Red Scale", 15) {
                            @Override
                            public void activate(ServerPlayerEntity player) {
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 200, 0));
                                player.sendMessage(Text.literal("Your blood pumps, granting you strength!"), true);
                            }
                        },
                        new Ability("Slicing Exorcism", 25) {
                            @Override
                            public void activate(ServerPlayerEntity player) {
                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 300, 1));
                                player.sendMessage(Text.literal("You sharpen your senses, moving faster."), true);
                            }
                        }
                )
        );
    }
}
