/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.bined.operation.swing.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import org.exbin.bined.operation.BinaryDataCommand;
import org.exbin.bined.operation.BinaryDataCompoundCommand;
import org.exbin.bined.operation.BinaryDataOperationException;
import org.exbin.bined.swing.CodeAreaCore;

/**
 * Class for compound command on hexadecimal document.
 *
 * @version 0.1.2 2016/12/20
 * @author ExBin Project (https://exbin.org)
 */
@ParametersAreNonnullByDefault
public class HexCompoundCommand extends CodeAreaCommand implements BinaryDataCompoundCommand {

    private final List<BinaryDataCommand> commands = new ArrayList<BinaryDataCommand>();

    public HexCompoundCommand(@Nonnull CodeAreaCore codeArea) {
        super(codeArea);
    }

    public static CodeAreaCommand buildCompoundCommand(CodeAreaCore codeArea, CodeAreaCommand... commands) {
        CodeAreaCommand resultCommand = null;
        for (CodeAreaCommand command : commands) {
            if (command != null) {
                if (resultCommand == null) {
                    resultCommand = command;
                } else if (resultCommand instanceof HexCompoundCommand) {
                    ((HexCompoundCommand) resultCommand).appendCommand(command);
                } else {
                    HexCompoundCommand compoundCommand = new HexCompoundCommand(codeArea);
                    compoundCommand.appendCommand(resultCommand);
                    compoundCommand.appendCommand(command);
                    resultCommand = compoundCommand;
                }
            }
        }

        return resultCommand;
    }

    @Nonnull
    @Override
    public CodeAreaCommandType getType() {
        return CodeAreaCommandType.COMPOUND;
    }

    @Override
    public void execute() throws BinaryDataOperationException {
        for (BinaryDataCommand command : commands) {
            command.execute();
        }
    }

    @Override
    public void redo() throws BinaryDataOperationException {
        for (BinaryDataCommand command : commands) {
            command.redo();
        }
    }

    @Override
    public void undo() throws BinaryDataOperationException {
        for (int i = commands.size() - 1; i >= 0; i--) {
            BinaryDataCommand command = commands.get(i);
            command.undo();
        }
    }

    @Override
    public boolean canUndo() {
        boolean canUndo = true;
        for (BinaryDataCommand command : commands) {
            if (!command.canUndo()) {
                canUndo = false;
                break;
            }
        }

        return canUndo;
    }

    @Override
    public void appendCommand(BinaryDataCommand command) {
        commands.add(command);
    }

    @Override
    public void appendCommands(Collection<BinaryDataCommand> commands) {
        commands.addAll(commands);
    }

    @Override
    public List<BinaryDataCommand> getCommands() {
        return commands;
    }

    @Override
    public boolean isEmpty() {
        return commands.isEmpty();
    }

    @Override
    public void dispose() throws BinaryDataOperationException {
        super.dispose();
        for (BinaryDataCommand command : commands) {
            command.dispose();
        }
    }
}
