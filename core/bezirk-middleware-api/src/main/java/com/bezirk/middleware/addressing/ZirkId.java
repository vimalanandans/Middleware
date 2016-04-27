/**
 * This file is part of Bezirk-Middleware-API.
 * <p>
 * Bezirk-Middleware-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * </p>
 * <p>
 * Bezirk-Middleware-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * </p>
 * You should have received a copy of the GNU General Public License
 * along with Bezirk-Middleware-API.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.bezirk.middleware.addressing;


/**
 * The middleware-generated identify of a Zirk. This ID is returned by
 * {@link com.bezirk.middleware.Bezirk#registerZirk(String)} and is used to interact with
 * the {@link com.bezirk.middleware.Bezirk} API.
 * <p>
 * The Bezirk middleware implements this interface.
 * </p>
 */
public interface ZirkId {
// For now, this is a marker interface because there is nothing to offer to a Zirk API wise.
}
