#!/usr/bin/env python
# -*- coding: utf-8 -*-

# ===============================================================================
#
#          FILE:  buildset.py
#
#       VERSION:  0.3
#
#   DESCRIPTION:  Used to build a KDE iconset from source icons.
#
#      REQUIRED:  Python-2.5.1, Python Imaging Library-1.1.6
#                 Versions are tested versions, other versions may or may not
#                 work.
#
#                 Iconsets with 128x128 (actions, apps, devices, filesystems,
#                 mimetypes) icons or iconsets with 128x128 (apps, devices,
#                 filesystems, mimetypes) and 32x32 (actions) icons.
#
#         USAGE:  python buildset.py 'iconsetname-version' (without quotes)
#
#                 Example: python buildset.py DarkGlass_Reworked
#
#                 This will give you a tarball named DarkGlass_Reworked.tar.bz2
#                 containing the icons in 128x128, 96x96, 72x72, 64x64, 56x56,
#                 48x48, 32x32, 22x22, 16x16 sizes along with any text files
#                 and buildscript files the iconset author included.
#
#        AUTHOR:  LocoMojo <slackinpenguin@gmail.com>
#       CREATED:  01/01/2008 11:40:27 PM EST
#       UPDATED:  02/10/2008 02:11:16 PM EST
#       LICENSE:  GPL
# ===============================================================================

# Test the Python installation.

try:
    import sys
    import os
    import os.path
    import tarfile
except:
    print '''
    Is Python installed?

    If so, please check your Python installation.

    If not, you can get Python from http://www.python.org/download/.

    Or you could check your distribution\'s repositories.

    Cannot continue with this script, exiting...
    '''
    sys.exit()

try:
    iconset = sys.argv[1]
except:
    print '''
    USAGE:  python buildset.py 'iconsetname-version' (without quotes)
 
            Example: python buildset.py Dark_Glass-2.7
 
            This will give you a tarball named Dark_Glass-2.7.tar.bz2
            containing the icons in 128x128, 96x96, 72x72, 64x64, 56x56, 
            48x48, 32x32, 22x22, 16x16 sizes along with any text files 
            and buildscript files the iconset author included.
    '''
    sys.exit()

# Check for Python Imaging Library.

try:
    from PIL import Image
except:
    print '''
    You need the Python Imaging Library (PIL).

    You can get it from http://www.pythonware.com/products/pil/ .

    Or you could check your distribution\'s repositories.

    This script cannot proceed, exiting...
    '''
    sys.exit()

# Test for source icons.

if os.path.isdir('128x128/'):
    pass
else:
    print '''
    There aren\'t any source icons here.

    Please run this script next to the 128x128 sized source icons.

    Exiting...
    '''
    sys.exit()

# Get subdirectories, including unusual ones.


def getSubDirList(list, dir):
    for i in os.listdir(dir):
        if not i.startswith('.'):
            list.append(i + '/')


# Iconset includes two sources, 128x128 and 32x32.


def scenario1():
    print 'Everything looks good, so far.'
    print ' '
    print 'Beginning the resizing process...'
    global source, source1, dirs, dirs1, dirs2, dirSv, size, size1, y, \
        z, subdirs, subdirs1, src, keep
    source = '128x128/'
    source1 = '32x32/'
    dirs = [
        '96x96/',
        '72x72/',
        '64x64/',
        '56x56/',
        '48x48/',
        '32x32/',
        '22x22/',
        '16x16/',
        ]
    dirs1 = ['96x96/', '72x72/', '64x64/', '56x56/', '48x48/']
    dirs2 = ['22x22/', '16x16/']
    dirSv = dirs2
    size = [
        96,
        72,
        64,
        56,
        48,
        32,
        22,
        16,
        ]
    size1 = [22, 16]
    list = []
    list1 = []
    getSubDirList(list, '128x128/')
    getSubDirList(list1, '32x32/')
    subdirs = list
    subdirs1 = list1
    y = 7
    z = 1
    src = 2
    keep = 1
    for i in dirs:
        for j in subdirs:
            if not os.path.isdir(i + j):
                os.makedirs(i + j)
    for i in dirs2:
        for j in subdirs1:
            if not os.path.isdir(i + j):
                os.makedirs(i + j)
    for i in subdirs:
        for j in os.listdir(source + i):
            if j.endswith('.xpm'):
                os.system('convert ' + source + i + j + ' ' + source + i
                           + j.strip('.xpm') + '.png')
                print 'Converted ' + source + i + j + ' '\
                     + 'to PNG format.'
    for i in subdirs1:
        for j in os.listdir(source1 + i):
            if j.endswith('.xpm'):
                os.system('convert ' + source1 + i + j + ' ' + source1
                           + i + j.strip('.xpm') + '.png')
                print 'Converted ' + source1 + i + j + ' '\
                     + 'to PNG format.'

    iconResize()


# Iconset includes one source, 128x128. All icons will be resized to all sizes.


def scenario2():
    print 'Everything looks good, so far.'
    print ' '
    print 'Beginning the resizing process...'
    print ' '
    global source, source1, dirs, size, y, z, subdirs, src
    source = '128x128/'
    source1 = source
    dirs = [
        '96x96/',
        '72x72/',
        '64x64/',
        '56x56/',
        '48x48/',
        '32x32/',
        '22x22/',
        '16x16/',
        ]
    size = [
        96,
        72,
        64,
        56,
        48,
        32,
        22,
        16,
        ]
    list = []
    y = 7
    z = 0
    src = 1
    getSubDirList(list, '128x128/')
    subdirs = list
    for i in dirs:
        for j in subdirs:
            os.makedirs(i + j)
    for i in subdirs:
        for j in os.listdir(source + i):
            if j.endswith('.xpm'):
                os.system('convert ' + source + i + j + ' ' + source + i
                           + j.strip('.xpm') + '.png')
                print 'Converted ' + source + i + j + ' '\
                     + 'to PNG format.'
    iconResize()


# Iconset includes one source, 128x128, but user prefers actions icons in 32x32 and smaller sizes only.


def scenario3():
    print 'Everything looks good, so far.'
    print ' '
    print 'Beginning the resizing process...'
    print ' '
    global source, source1, dirs, dirs1, dirSv, size, size1, y, z, \
        subdirs, subdirs1, src, keep
    source = '128x128/'
    source1 = source
    dirs = [
        '96x96/',
        '72x72/',
        '64x64/',
        '56x56/',
        '48x48/',
        '32x32/',
        '22x22/',
        '16x16/',
        ]
    dirs1 = ['32x32/', '22x22/', '16x16/']
    dirSv = dirs1
    size = [
        96,
        72,
        64,
        56,
        48,
        32,
        22,
        16,
        ]
    size1 = [32, 22, 16]
    list = []
    list1 = []
    getSubDirList(list, '128x128/')
    list.remove('actions/')
    list1.append('actions/')
    subdirs = list
    subdirs1 = list1
    keep = 2
    src = 1
    z = 2
    for i in dirs:
        for j in subdirs:
            os.makedirs(i + j)
    for i in dirs1:
        for j in subdirs1:
            os.makedirs(i + j)
    for i in subdirs:
        for j in os.listdir(source + i):
            if j.endswith('.xpm'):
                os.system('convert ' + source + i + j + ' ' + source + i
                           + j.strip('.xpm') + '.png')
                print 'Converted ' + source + i + j + ' '\
                     + 'to PNG format.'
    iconResize()


# One or two source layout? If two source, ask user about actions icons.


def checkLayout():
    if os.path.isdir('32x32/'):
        scenario1()
    else:
        trim()


# User decision.


def trim():
    ans = \
        raw_input('''
            We are dealing with one source of icons, namely 128x128 sized icons. It seems the iconset
            author has intended for all icons to be resized to all sizes, 128x128 to 16x16.

            However, actions icons are typically used in menus so they don\'t really need to be any larger
            than 32x32. You could choose to have actions icons in all sizes if you think you may use them
            for purposes other than menus or you could choose to use your disk space more efficiently by 
            choosing to keep actions icons at menu sizes, 32x32 and smaller. If you wish to save space by 
            keeping the iconset trimmed, type "y", otherwise type "n" to have actions icons in all sizes.

            [y or n] ''')
    print ' '
    if ans == 'y' or ans == 'yes':
        print 'Ok, actions icons will only be in 32x32 and smaller sizes.'
        scenario3()
    elif ans == 'n' or ans == 'no':
        print 'Ok, actions icons will be in all sizes, 128x128 to 16x16.'
        scenario2()


# Apps, devices, filesystems, and mimetypes icons resized here.


def iconResize():
    x = 0
    list = []
    while x <= 7:
        for i in subdirs:
            cursize = (size[x], size[x])
            print ' '
            print 'Resizing ', i, 'icons to', size[x], 'x', size[x]
            for j in os.listdir(source + i):
                if not j.startswith('.'):
                    try:
                        (file, ext) = os.path.splitext(j)
                        im = Image.open(source + i + j)
                        im.thumbnail(cursize, Image.ANTIALIAS)
                        im.save(dirs[x] + i + file + '.png')
                    except:
                        print 'Cannot resize', j, 'skipping...'
                        list.append(source + i + j)
        x = x + 1
    if len(list) > 0:
        f = open('failed.txt', 'a')
        for i in list:
            d = {}
            for x in list:
                d[x] = x
            failed = d.values()
        for i in failed:
            if len(i) > 1:
                f.write(i + '\n')
        f.close()
    if z == 0:
        createTarBall()
    else:
        actResize()


# Actions icons resized here.


def actResize():
    x = 0
    list = []
    while x <= z:
        for i in subdirs1:
            cursize = (size1[x], size1[x])
            print ' '
            print 'Resizing ', i, 'icons to', size1[x], 'x', size1[x]
            for j in os.listdir(source1 + i):
                if not j.startswith('.'):
                    try:
                        (file, ext) = os.path.splitext(j)
                        im = Image.open(source1 + i + j)
                        im.thumbnail(cursize, Image.ANTIALIAS)
                        im.save(dirSv[x] + i + file + '.png')
                    except:
                        print 'Cannot resize', j, 'skipping...'
                        list.append(source1 + i + j)
        x = x + 1
    if len(list) > 0:
        f = open('failed.txt', 'a')
        for i in list:
            d = {}
            for x in list:
                d[x] = x
            failed = d.values()
        for i in failed:
            if len(i) > 1:
                f.write(i + '\n')
        f.close()
    createTarBall()


# Create parent directory and copy/move subdirectories and files into it.


def createTarBall():
    print ' '
    print 'Resizing complete, beginning tar and compression...'
    os.mkdir(iconset)
    if src == 1:
        os.system('cp -r 128x128 ' + iconset)
        for i in os.listdir('.'):
            if os.path.isfile(i):
                os.system('cp ' + i + ' ' + iconset)
        for i in dirs:
            os.system('mv ' + i + ' ' + iconset)
    elif src == 2:
        os.system('cp -r 128x128/ 32x32/ ' + iconset)
        for i in os.listdir('.'):
            if os.path.isfile(i):
                os.system('cp ' + i + ' ' + iconset)
        for i in dirs1 + dirs2:
            os.system('mv ' + i + ' ' + iconset)

# Begin tar and compression process.

    tar = tarfile.open(iconset + '.tar.bz2', 'w:bz2')
    tar.add(iconset)
    tar.close()
    os.system('rm -rf ' + iconset)
    if src == 2:
        if keep == 1:
            for i in os.listdir('32x32/'):
                if not i == 'actions':
                    os.system('rm -rf 32x32/' + i)
        elif keep == 2:
            os.system('rm -rf 32x32/')
    report()


def report():
    print ' '
    print 'Job is complete.\n'
    if os.path.isfile('failed.txt'):
        print 'However, the following has failed to resize for some reason:'
        f = open('failed.txt', 'r')
        for line in f.readlines():
            print line
        f.close()
        print 'You may want to investigate further.'
        print ' '
        print 'NOTE: You can probably safely ignore ".xpm" errors as they\'ve hopefully been'
        print 'converted with ImageMagick\'s convert. You may want to doublecheck, just in case.'
        print ' '
        print 'For your convenience, you will find a text file in this directory'
        print 'called "failed.txt" which lists the failed files in question.'
    print ' '
    print '''
    You can now install this iconset via k-control by
    selecting the newly created tarball.

    Enjoy! If you find any bugs, please let me know at kde-look.org
    '''


checkLayout()
