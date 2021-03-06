/*******************************************************************************
 *
 *   Copyright 2016 Mytech Ingenieria Aplicada <http://www.mytechia.com>
 *   Copyright 2016 Gervasio Varela <gervasio.varela@mytechia.com>
 *
 *   This file is part of Robobo Emotion Module.
 *
 *   Robobo Emotion Module is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Lesser General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   Robobo Emotion Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with Robobo Emotion Module.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package com.mytechia.robobo.framework.hri.emotion;

/** Enumeration of support Robobo emotions
 */
public enum Emotion {

    HAPPY,
    SAD,
    ANGRY,
    SMYLING,
    LAUGHING,
    EMBARRASSED,
    SURPRISED,
    IN_LOVE,
    NORMAL,
    SLEEPING;

    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    public static Emotion fromString(String s){
        switch (s){
            case "happy":
                return Emotion.HAPPY;
                
            case "laughing":
                return Emotion.LAUGHING;
                
            case "sad":
                return Emotion.SAD;
                
            case "angry":
                return Emotion.ANGRY;
                
            case "surprised":
                return Emotion.SURPRISED;
                
            case "normal":
                return Emotion.NORMAL;

            case "sleeping":
                return Emotion.SLEEPING;

            case "embarrassed":
                return Emotion.EMBARRASSED;

            case "inlove":
                return Emotion.IN_LOVE;
                
            default:
                return  null;
        }
    }
}
