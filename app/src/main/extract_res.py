import os
import xml.etree.ElementTree as ET
import shutil 

def is_translatable_resource(path):
	if not path.endswith(".xml"): return False
	if path.endswith("strings.xml"): return True

	tree = ET.parse(path)
	root = tree.getroot()

	if root.tag != "resources": return False

	to_look = [*root]
	for child in to_look:
		if child.tag in ["string", "item"] and child.attrib.get("translatable") != 'false':
			return True

		if child.tag == "string-array":
			to_look.extend([*child])

	return False


def main():

	os.mkdir(f".{os.path.sep}translatable")

	resources = []
	for (root, dirs, files) in os.walk(f".{os.path.sep}res", "topdown"):
		if len(dirs) > 0 : 
			continue

		[resources.append(f"{root}{os.path.sep}{x}") for x in files]

	translatable = [x for x in resources if is_translatable_resource(x)]
	print("\n".join(translatable))

	to_copy = [(x, f".{os.path.sep}translatable{os.path.sep}{os.path.sep.join(x.split(os.path.sep)[1:]).replace(os.path.sep, '_')}") for x in translatable]
	print("\n".join(map(lambda x: str(x[0]) + " -> " + str(x[1]), to_copy)))
	[shutil.copy(x[0], x[1]) for x in to_copy]



if __name__ == "__main__":
	main()