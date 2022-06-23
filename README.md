# Domain(s)-specialist viewpoint for system schematics addon for Capella (MBSE)

## Purpose
In Cyber Physic system, most of the architecture work is done within a schematic tool (Autodesk Autocad, etc) for designing the system and its corresponding physical architecture realized by (mostly) hardware components (pumps, etc).

However this (usual and classic) way of proceeding does not ease for moving up to the architecture levels (logical, system and operational), leading to inconsistencies between domain-specialists (electrical, hydraulic, etc) and even more with the upper system-level.

This add-on permits a domain-specialist engineer to work with "block and link" simplified representations and be able to generate the domain-specialist representation from it (one-click process).

## How it works
A "block" within a Physical Architecture Blank will be "replaced" dynamically with its right domain-specific visual representation, according to the property values set for this block. The (pre-defined by settings) property values map the "block" with a component type or material from a (pre-defined by settings) catalog, allowing its dynamic replacement at runtime and taking care of interfaces/links positionning.

## Visuals
TODO

## Installation
Download the build and unzip it in the dropins directory within your Capella installation. Restart Capella.
Then activate the corresponding viewpoint in Capella to enable it.

> Please consult the releases page for the supported Capella's versions.

## Usage
Set the right settings (look at the documentation within the Capella help) for importing the visual figures for each domain-specialist representation according to the right properties you are going to assign to each component.

Model your physical architecture (within the Physical Architecture, with a "Physical Architecture Blank" type diagramme) as you usually do (business as usual).

Select the Domain(s)-specialist viewpoint for system schematics for this diagramme and the magic happens. A new diagramme is generated !

> Disclaimer: this addon is delivered as-is and we are not liable of any data loss or data corruption your project may occur by using it.

## Support
Unfortunatly we do not provide any assistance or support for the addon usage. Please use as much as possible the Gitlab forum for help and/or assistance as we hope the community will participate and contribute.

## Roadmap
To date, we do not plan any enhancements however if you have ideas for releases in the future, feel free to contact us or propose it in the forum.

Regarding the maintenance planning for supporting the next Capella releases, source code is at your disposal and we will be happy to merge your new branch proposal or PR.

## Contributing
Feel free to help us, we are open to any assistance or support.

## Authors and acknowledgment
Thank you to all contributors that made it possible.
Copyright 2022 Naval Group SA

## License
This project is under the EPL2.0

## Project status
We run out of energy or time for this project, development has slowed down. You can make us an explicit request for becoming a maintainer.
