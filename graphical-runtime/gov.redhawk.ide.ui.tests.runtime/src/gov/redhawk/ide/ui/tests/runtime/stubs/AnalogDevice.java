/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.ui.tests.runtime.stubs;

import org.eclipse.emf.common.util.URI;
import org.ossie.component.Device;

import CF.DataType;
import CF.ExecutableDeviceOperations;
import CF.FileSystem;
import CF.InvalidFileName;
import CF.DevicePackage.InvalidState;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.ExecutableDevicePackage.InvalidFunction;
import CF.ExecutableDevicePackage.InvalidOptions;
import CF.ExecutableDevicePackage.InvalidParameters;
import CF.ExecutableDevicePackage.InvalidProcess;
import CF.LoadableDevicePackage.InvalidLoadKind;
import CF.LoadableDevicePackage.LoadFail;
import CF.LoadableDevicePackage.LoadType;

public class AnalogDevice extends Device implements ExecutableDeviceOperations {

	@Override
	public void load(FileSystem fs, String fileName, LoadType loadKind) throws InvalidState, InvalidLoadKind, InvalidFileName, LoadFail {
	}

	@Override
	public void unload(String fileName) throws InvalidState, InvalidFileName {
	}

	@Override
	public void run() {
	}

	@Override
	public void terminate(int processId) throws InvalidProcess, InvalidState {
	}

	@Override
	public int execute(String name, DataType[] options, DataType[] parameters)
		throws InvalidState, InvalidFunction, InvalidParameters, InvalidOptions, InvalidFileName, ExecuteFail {
		return 0;
	}

	@Override
	public int executeLinked(String name, DataType[] options, DataType[] parameters, String[] deps)
		throws InvalidState, InvalidFunction, InvalidParameters, InvalidOptions, InvalidFileName, ExecuteFail {
		return 0;
	}

	@Override
	public String softwareProfile() {
		URI uri = URI.createPlatformPluginURI("gov.redhawk.ide.ui.tests.runtime/resources/analogDevice/analogDevice.spd.xml", true);
		return uri.toString();
	}

	@Override
	public String identifier() {
		return "analogDevice";
	}

	@Override
	public String label() {
		return "analogDevice";
	}

}
