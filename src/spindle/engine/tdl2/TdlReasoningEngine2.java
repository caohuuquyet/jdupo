/**
 * SPINdle (version 2.2.4)
 * Copyright (C) 2009-2014 NICTA Ltd.
 *
 * This file is part of SPINdle project.
 * 
 * SPINdle is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * SPINdle is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SPINdle.  If not, see <http://www.gnu.org/licenses/>.
 *
 * @author H.-P. Lam (oleklam@gmail.com), National ICT Australia - Queensland Research Laboratory 
 */
package spindle.engine.tdl2;

import spindle.engine.ReasoningEngineException;
import spindle.engine.mdl.MdlReasoningEngine2;
import spindle.sys.AppConst;
import spindle.sys.message.ErrorMessage;

public class TdlReasoningEngine2 extends MdlReasoningEngine2 {

	public TdlReasoningEngine2() throws ReasoningEngineException {
		super();
		if (AppConst.isDeploy)
			throw new ReasoningEngineException(getClass(), ErrorMessage.REASONING_ENGINE_NOT_SUPPORTED, "TDL");
	}

}
